package com.example.minicorebank.controller;

import com.example.minicorebank.dto.AccountTxItem;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.CustomerRepository;
import com.example.minicorebank.security.AuthUser;
import com.example.minicorebank.service.ReadTxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ✅ API lấy tài khoản của chính user đang login
    @GetMapping("/account")
    public AccountEntity myAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUser me)) {
            throw new AccessDeniedException("Unauthenticated");
        }
        if (!"CUSTOMER".equals(me.role()) || me.customerId() == null) {
            throw new AccessDeniedException("Only customer can access");
        }

        return accountRepository.findTopByCustomerEntity_IdOrderByIdAsc(me.customerId())
                .orElseThrow(() -> new IllegalStateException("Customer has no account"));
    }

    @GetMapping("/customers/{id}")
    public CustomerEntity getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @GetMapping("/accounts/{id}")
    public AccountEntity getAccount(@PathVariable Long id) {
        AccountEntity a = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUser me) {
            if ("CUSTOMER".equals(me.role())) {
                Long ownerCustomerId = a.getCustomerEntity().getId();
                if (me.customerId() == null || !ownerCustomerId.equals(me.customerId())) {
                    throw new AccessDeniedException("Forbidden");
                }
            }
        }
        return a;
    }

    @GetMapping("/accounts/{id}/transactions")
    public Page<AccountTxItem> list(
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
    ) {
        Instant fromTs = (from == null || from.isBlank()) ? null : Instant.parse(from);
        Instant toTs = (to == null || to.isBlank()) ? null : Instant.parse(to);

        return readTxService.listAccountTx(
                accountId, direction, type, status, fromTs, toTs, minAmount, maxAmount, q, pageable
        );
    }
}