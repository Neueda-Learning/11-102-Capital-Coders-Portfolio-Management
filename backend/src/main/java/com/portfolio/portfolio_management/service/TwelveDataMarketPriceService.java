package com.portfolio.portfolio_management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwelveDataMarketPriceService implements MarketPriceService {

    private final RestClient restClient;
    private final String apiKey;
    private final Duration cacheTtl;
    private final Map<String, CachedQuote> quoteCache = new ConcurrentHashMap<>();

    public TwelveDataMarketPriceService(
            @Value("${market.price.twelve-data.base-url:https://api.twelvedata.com}") String baseUrl,
            @Value("${market.price.twelve-data.api-key:}") String apiKey,
            @Value("${market.price.twelve-data.cache-ms:120000}") long cacheMs
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.cacheTtl = Duration.ofMillis(Math.max(cacheMs, 1000));
    }

    @Override
    public double getLatestPrice(String tickerSymbol) {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            throw new IllegalArgumentException("Ticker symbol is required for live pricing.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Twelve Data API key is not configured.");
        }

        String tickerKey = tickerSymbol.trim().toUpperCase(Locale.ROOT);
        CachedQuote cachedQuote = quoteCache.get(tickerKey);
        if (cachedQuote != null && !cachedQuote.isExpired(cacheTtl)) {
            return cachedQuote.price();
        }

        Map<?, ?> payload = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/price")
                        .queryParam("symbol", tickerKey)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .body(Map.class);

        if (payload == null) {
            throw new IllegalArgumentException("Unable to fetch live market price.");
        }

        Object status = payload.get("status");
        if (status != null && "error".equalsIgnoreCase(String.valueOf(status))) {
            Object message = payload.get("message");
            throw new IllegalArgumentException("Live market price fetch failed: " + message);
        }

        Object priceRaw = payload.get("price");
        if (priceRaw == null) {
            throw new IllegalArgumentException("Live market price is unavailable for ticker " + tickerSymbol + ".");
        }

        try {
            double latestPrice = Double.parseDouble(String.valueOf(priceRaw));
            quoteCache.put(tickerKey, new CachedQuote(latestPrice, Instant.now()));
            return latestPrice;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Received invalid live market price for ticker " + tickerSymbol + ".");
        }
    }

    private record CachedQuote(double price, Instant fetchedAt) {
        private boolean isExpired(Duration ttl) {
            return fetchedAt.plus(ttl).isBefore(Instant.now());
        }
    }
}

