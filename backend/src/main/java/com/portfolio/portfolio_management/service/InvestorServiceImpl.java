package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicateInvestorEmailException;
import com.portfolio.portfolio_management.exception.InvestorNotFoundException;
import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.repository.InvestorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestorServiceImpl implements InvestorService {

    private final InvestorRepository investorRepository;

    public InvestorServiceImpl(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    @Override
    public List<Investor> getAllInvestors() {
        return investorRepository.getAllInvestors();
    }

    @Override
    public Investor getInvestorById(Integer investorId) {
        return investorRepository
                .getInvestorById(investorId)
                .orElseThrow(() -> new InvestorNotFoundException(investorId));
    }

    @Override
    public Investor addInvestor(Investor investor) {
        investorRepository.findByEmail(investor.investorEmail())
                .ifPresent(existing -> {
                    throw new DuplicateInvestorEmailException(investor.investorEmail());
                });

        return investorRepository.addInvestor(investor);
    }

    @Override
    public Investor updateInvestor(Integer investorId, Investor investor) {
        investorRepository.getInvestorById(investorId)
                .orElseThrow(() -> new InvestorNotFoundException(investorId));

        investorRepository.findByEmail(investor.investorEmail())
                .ifPresent(existing -> {
                    if (existing.investorId() != investorId) {
                        throw new DuplicateInvestorEmailException(investor.investorEmail());
                    }
                });

        return investorRepository.updateInvestor(investorId, investor);
    }

    @Override
    public void deleteInvestor(Integer investorId) {
        investorRepository.getInvestorById(investorId)
                .orElseThrow(() -> new InvestorNotFoundException(investorId));

        investorRepository.deleteInvestor(investorId);
    }
}