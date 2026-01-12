package com.example.minicorebank.controller;

import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/customers/{id}")
    public CustomerEntity getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @GetMapping("/accounts/{id}")
    public AccountEntity getAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }
}
