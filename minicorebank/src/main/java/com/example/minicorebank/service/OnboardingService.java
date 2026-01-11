package com.example.minicorebank.service;

import com.example.minicorebank.dto.CreateAccountRequest;
import com.example.minicorebank.dto.CreateCustomerRequest;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public CustomerEntity createCustomer(CreateCustomerRequest req) {
        CustomerEntity c = new CustomerEntity();
        c.setFullName(req.fullName());
        c.setPhone(req.phone());
        c.setEmail(req.email());
        c.setStatus("ACTIVE");
        return customerRepository.save(c);
    }

    @Transactional
    public AccountEntity openPaymentAccount(CreateAccountRequest req) {
        CustomerEntity c = customerRepository.findById(req.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId()));

        AccountEntity a = new AccountEntity();
        a.setCustomerEntity(c);
        a.setAccountNo(generateAccountNo());
        a.setType("PAYMENT");
        a.setStatus("ACTIVE");
        a.setCurrency("VND");

        return accountRepository.save(a);
    }

    private String generateAccountNo() {
        while (true) {
            long r = Math.abs(ThreadLocalRandom.current().nextLong()) % 1_000_000_0000L;
            String acc = "CB" + String.format("%010d", r);
            if (!accountRepository.existsByAccountNo(acc)) return acc;
        }
    }
}
