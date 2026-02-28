package com.example.minicorebank.service;
import com.example.minicorebank.repository.AccountRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OwnershipService {
    private final AccountRepository accountRepo;

    public OwnershipService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void assertCustomerOwnsAccount(Long customerId, Long accountId) {
        boolean ok = accountRepo.existsByIdAndCustomerEntity_Id(accountId, customerId);
        if (!ok) throw new AccessDeniedException("Account not owned by customer");
    }
}