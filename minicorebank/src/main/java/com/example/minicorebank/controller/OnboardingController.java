package com.example.minicorebank.controller;

import com.example.minicorebank.dto.CreateAccountRequest;
import com.example.minicorebank.dto.CreateCustomerRequest;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OnboardingController {
    private final OnboardingService service;


    @PostMapping("/customers")
    public CustomerEntity createCustomer(@Valid @RequestBody CreateCustomerRequest req){
        return  service.createCustomer(req);
    }

    @PostMapping("/accounts")
    public AccountEntity openAccount(@Valid @RequestBody CreateAccountRequest req){
        return service.openPaymentAccount(req);
    }
}
