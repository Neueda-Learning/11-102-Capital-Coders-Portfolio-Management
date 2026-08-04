package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicateFundException;
import com.portfolio.portfolio_management.exception.FundNotfoundException;
import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.repository.FundRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FundServiceImpl implements FundService {

    private final FundRepository fundRepository;

    public FundServiceImpl(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }

    @Override
    public Optional<Fund> getFundById(int fundId) {
        return fundRepository.getFundById(fundId);
    }

    @Override
    public Fund addnewFund(Fund fund) {
        fundRepository.getFundByName(fund.fundName()).ifPresent(existingFund -> {
            throw new DuplicateFundException(fund.fundName());
        });
        return fundRepository.addnewFund(fund);
    }

    @Override
    public Fund updateFund(Fund fund) {
        fundRepository.getFundById(fund.fundId()).orElseThrow(() -> new FundNotfoundException(fund.fundId()));
        return fundRepository.updateFund(fund);
    }

    @Override
    public void deleteFund(int fundId) {
        fundRepository.getFundById(fundId).orElseThrow(() -> new FundNotfoundException(fundId));
        fundRepository.deleteFund(fundId);
    }
}
