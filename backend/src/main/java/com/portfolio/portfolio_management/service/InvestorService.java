package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.repository.InvestorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvestorService {

	private final InvestorRepository investorRepository;

	public InvestorService(InvestorRepository investorRepository) {
		this.investorRepository = investorRepository;
	}

	public List<Investor> getInvestors() {
		return investorRepository.findAll();
	}

	public Investor getInvestorById(int investorId) {
		return investorRepository.findById(investorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with id: " + investorId));
	}

	public Investor addInvestor(Investor investor) {
		investor.setInvestorId(null);
		return investorRepository.save(investor);
	}

	public Investor updateInvestor(int investorId, Investor investor) {
		getInvestorById(investorId);
		investor.setInvestorId(investorId);
		return investorRepository.save(investor);
	}

	public void deleteInvestor(int investorId) {
		getInvestorById(investorId);
		investorRepository.deleteById(investorId);
	}
}

