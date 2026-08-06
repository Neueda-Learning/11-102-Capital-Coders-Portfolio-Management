package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.NewsArticle;

import java.util.List;

public interface NewsService {

    /**
     * Fetches recent market news.
     *
     * @param query   free-text search term, e.g. "stock market" or a company name
     * @param pageSize how many articles to return (capped by the implementation)
     */
    List<NewsArticle> getNews(String query, int pageSize);
}
