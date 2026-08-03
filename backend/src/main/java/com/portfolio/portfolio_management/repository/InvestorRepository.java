package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Investor;
import org.springframework.data.repository.ListCrudRepository;

public interface InvestorRepository extends ListCrudRepository<Investor, Integer> {
}

