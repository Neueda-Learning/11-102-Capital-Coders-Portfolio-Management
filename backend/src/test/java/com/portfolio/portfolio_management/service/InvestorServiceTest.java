package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicateInvestorEmailException;
import com.portfolio.portfolio_management.exception.InvestorNotFoundException;
import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.repository.InvestorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestorServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    private InvestorServiceImpl investorService;

    @BeforeEach
    void setUp() {
        investorService = new InvestorServiceImpl(investorRepository);
    }

    private Investor sampleInvestor(int id, String email) {
        return new Investor(id, "Investor A", email);
    }

    @Test
    @DisplayName("getAllInvestors returns repository list")
    void getAllInvestors_returnsList() {
        when(investorRepository.getAllInvestors()).thenReturn(List.of(
                sampleInvestor(1, "a@investor.com"),
                sampleInvestor(2, "b@investor.com")
        ));

        List<Investor> result = investorService.getAllInvestors();

        assertEquals(2, result.size());
        verify(investorRepository).getAllInvestors();
    }

    @Test
    @DisplayName("getInvestorById throws when investor is missing")
    void getInvestorById_whenMissing_throwsNotFound() {
        when(investorRepository.getInvestorById(99)).thenReturn(Optional.empty());

        InvestorNotFoundException ex = assertThrows(
                InvestorNotFoundException.class,
                () -> investorService.getInvestorById(99)
        );

        assertEquals("Investor with ID 99 not found.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestor throws conflict when email exists")
    void addInvestor_whenDuplicateEmail_throwsDuplicate() {
        Investor request = sampleInvestor(0, "dup@investor.com");
        when(investorRepository.findByEmail("dup@investor.com")).thenReturn(Optional.of(sampleInvestor(6, "dup@investor.com")));

        DuplicateInvestorEmailException ex = assertThrows(
                DuplicateInvestorEmailException.class,
                () -> investorService.addInvestor(request)
        );

        assertEquals("Investor with email 'dup@investor.com' already exists.", ex.getMessage());
        verify(investorRepository, never()).addInvestor(request);
    }

    @Test
    @DisplayName("addInvestor stores investor when email is unique")
    void addInvestor_whenUnique_addsInvestor() {
        Investor request = sampleInvestor(0, "new@investor.com");
        Investor created = sampleInvestor(10, "new@investor.com");
        when(investorRepository.findByEmail("new@investor.com")).thenReturn(Optional.empty());
        when(investorRepository.addInvestor(request)).thenReturn(created);

        Investor result = investorService.addInvestor(request);

        assertEquals(10, result.investorId());
        verify(investorRepository).addInvestor(request);
    }

    @Test
    @DisplayName("updateInvestor throws when investor is missing")
    void updateInvestor_whenMissing_throwsNotFound() {
        Investor request = sampleInvestor(0, "update@investor.com");
        when(investorRepository.getInvestorById(30)).thenReturn(Optional.empty());

        InvestorNotFoundException ex = assertThrows(
                InvestorNotFoundException.class,
                () -> investorService.updateInvestor(30, request)
        );

        assertEquals("Investor with ID 30 not found.", ex.getMessage());
        verify(investorRepository, never()).updateInvestor(30, request);
    }

    @Test
    @DisplayName("updateInvestor throws conflict when email belongs to another investor")
    void updateInvestor_whenDuplicateEmailForOther_throwsDuplicate() {
        Investor request = sampleInvestor(0, "dup@investor.com");
        when(investorRepository.getInvestorById(4)).thenReturn(Optional.of(sampleInvestor(4, "old@investor.com")));
        when(investorRepository.findByEmail("dup@investor.com")).thenReturn(Optional.of(sampleInvestor(12, "dup@investor.com")));

        DuplicateInvestorEmailException ex = assertThrows(
                DuplicateInvestorEmailException.class,
                () -> investorService.updateInvestor(4, request)
        );

        assertEquals("Investor with email 'dup@investor.com' already exists.", ex.getMessage());
        verify(investorRepository, never()).updateInvestor(4, request);
    }

    @Test
    @DisplayName("updateInvestor allows same email for same investor")
    void updateInvestor_whenSameEmailForSameInvestor_updates() {
        Investor request = sampleInvestor(0, "same@investor.com");
        Investor updated = sampleInvestor(4, "same@investor.com");

        when(investorRepository.getInvestorById(4)).thenReturn(Optional.of(sampleInvestor(4, "same@investor.com")));
        when(investorRepository.findByEmail("same@investor.com")).thenReturn(Optional.of(sampleInvestor(4, "same@investor.com")));
        when(investorRepository.updateInvestor(4, request)).thenReturn(updated);

        Investor result = investorService.updateInvestor(4, request);

        assertEquals(4, result.investorId());
        verify(investorRepository).updateInvestor(4, request);
    }

    @Test
    @DisplayName("deleteInvestor throws when investor is missing")
    void deleteInvestor_whenMissing_throwsNotFound() {
        when(investorRepository.getInvestorById(22)).thenReturn(Optional.empty());

        InvestorNotFoundException ex = assertThrows(
                InvestorNotFoundException.class,
                () -> investorService.deleteInvestor(22)
        );

        assertEquals("Investor with ID 22 not found.", ex.getMessage());
        verify(investorRepository, never()).deleteInvestor(22);
    }

    @Test
    @DisplayName("deleteInvestor calls repository when investor exists")
    void deleteInvestor_whenFound_deletes() {
        when(investorRepository.getInvestorById(3)).thenReturn(Optional.of(sampleInvestor(3, "a@investor.com")));

        investorService.deleteInvestor(3);

        verify(investorRepository).deleteInvestor(3);
    }
}

