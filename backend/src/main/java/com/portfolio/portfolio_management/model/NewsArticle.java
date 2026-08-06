package com.portfolio.portfolio_management.model;

public record NewsArticle(
        String title,
        String description,
        String url,
        String imageUrl,
        String sourceName,
        String publishedAt
) {
}
