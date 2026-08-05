package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;
import com.portfolio.portfolio_management.repository.FundRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundServiceImpl implements FundService {


    private final FundRepository fundRepository;


    public FundServiceImpl(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }


    @Override
    public List<Fund> getFundsByInvestorId(int investorId) {

        return fundRepository.getFundsByInvestorId(investorId);
    }


    @Override
    public FundSummary getTotalFundsByInvestorId(int investorId) {

        return fundRepository.getTotalFundsByInvestorId(investorId);
    }


    @Override
    public Fund addnewFund(Fund fund) {

        return fundRepository.addnewFund(fund);
    }


    @Override
    public Fund updateFund(Fund fund) {

        return fundRepository.updateFund(fund);
    }


    @Override
    public void deleteFund(int fundId) {

        fundRepository.deleteFund(fundId);
    }
}