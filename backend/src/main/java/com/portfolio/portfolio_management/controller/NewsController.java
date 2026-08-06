package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.NewsArticle;
import com.portfolio.portfolio_management.service.NewsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * GET /news                     -> general market news
     * GET /news?company=Apple       -> news filtered to a company/asset name
     * GET /news?company=Apple&pageSize=5
     */
    @GetMapping
    public List<NewsArticle> getNews(
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "12") int pageSize
    ) {
        String query = (company == null || company.isBlank()) ? "stock market" : company;
        return newsService.getNews(query, pageSize);
    }
}
