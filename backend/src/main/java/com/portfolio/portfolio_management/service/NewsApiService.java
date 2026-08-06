package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NewsApiService implements NewsService {

    private final RestClient restClient;
    private final String apiKey;
    private final Duration cacheTtl;
    private final Map<String, CachedNews> newsCache = new ConcurrentHashMap<>();

    public NewsApiService(
            @Value("${news.api.base-url:https://newsapi.org/v2}") String baseUrl,
            @Value("${news.api.key:}") String apiKey,
            @Value("${news.api.cache-ms:300000}") long cacheMs
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.cacheTtl = Duration.ofMillis(Math.max(cacheMs, 1000));
    }

    @Override
    public List<NewsArticle> getNews(String query, int pageSize) {
        String searchTerm = (query == null || query.isBlank()) ? "stock market" : query.trim();
        int safePageSize = pageSize <= 0 ? 12 : Math.min(pageSize, 30);

        String cacheKey = searchTerm.toLowerCase(Locale.ROOT) + "|" + safePageSize;
        CachedNews cached = newsCache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheTtl)) {
            return cached.articles();
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("News API key is not configured.");
        }

        Map<?, ?> payload = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/everything")
                        .queryParam("q", searchTerm)
                        .queryParam("sortBy", "publishedAt")
                        .queryParam("language", "en")
                        .queryParam("pageSize", safePageSize)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(Map.class);

        if (payload == null) {
            throw new IllegalArgumentException("Unable to fetch market news.");
        }

        Object status = payload.get("status");
        if (status != null && "error".equalsIgnoreCase(String.valueOf(status))) {
            Object message = payload.get("message");
            throw new IllegalArgumentException("Market news fetch failed: " + message);
        }

        Object articlesRaw = payload.get("articles");
        List<NewsArticle> articles = new ArrayList<>();
        if (articlesRaw instanceof List<?> rawList) {
            for (Object item : rawList) {
                if (item instanceof Map<?, ?> articleMap) {
                    articles.add(toNewsArticle(articleMap));
                }
            }
        }

        newsCache.put(cacheKey, new CachedNews(articles, Instant.now()));
        return articles;
    }

    private NewsArticle toNewsArticle(Map<?, ?> articleMap) {
        String title = asString(articleMap.get("title"));
        String description = asString(articleMap.get("description"));
        String url = asString(articleMap.get("url"));
        String imageUrl = asString(articleMap.get("urlToImage"));
        String publishedAt = asString(articleMap.get("publishedAt"));

        String sourceName = null;
        Object sourceRaw = articleMap.get("source");
        if (sourceRaw instanceof Map<?, ?> sourceMap) {
            sourceName = asString(sourceMap.get("name"));
        }

        return new NewsArticle(title, description, url, imageUrl, sourceName, publishedAt);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private record CachedNews(List<NewsArticle> articles, Instant fetchedAt) {
        private boolean isExpired(Duration ttl) {
            return fetchedAt.plus(ttl).isBefore(Instant.now());
        }
    }
}
