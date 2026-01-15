package com.example.minicorebank.controller;

import com.example.minicorebank.dto.AccountTxItem;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.CustomerRepository;
import com.example.minicorebank.service.ReadTxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final ReadTxService readTxService;

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

    @GetMapping("/accounts/{id}/transactions")
    public Page<AccountTxItem> List(
            @PathVariable("id") Long accountId,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String q,
            Pageable pageable
            ){
        Instant fromTs = (from == null || from.isBlank()) ? null : Instant.parse(from);
        Instant toTs = (to == null || to.isBlank()) ? null : Instant.parse(to);

        return readTxService.listAccountTx(
                accountId,direction,type,status,fromTs,toTs,maxAmount,minAmount,q,pageable
                );
    }

}
