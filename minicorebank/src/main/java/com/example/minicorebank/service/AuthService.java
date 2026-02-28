package com.example.minicorebank.service;


import com.example.minicorebank.dto.LoginReq;
import com.example.minicorebank.dto.RegisterCustomerReq;
import com.example.minicorebank.dto.RegisterEmployeeReq;
import com.example.minicorebank.dto.TokenRes;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.CustomerEntity;
import com.example.minicorebank.entity.UserEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.CustomerRepository;
import com.example.minicorebank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final AccountRepository accountRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public  AuthService(UserRepository userRepo,CustomerRepository customerRepo, AccountRepository accountRepo, PasswordEncoder encoder, JwtService jwt){
        this.userRepo =userRepo;
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public TokenRes registerCustomer(RegisterCustomerReq req) {
        if (userRepo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("username already exists");
        }
            // 1) customers
            CustomerEntity c = new CustomerEntity();
            c.setFullName(req.fullName());
            c.setPhone(req.phone());
            c.setEmail(req.email());
            c.setStatus("ACTIVE");
            c = customerRepo.save(c);

            // 2) accounts
            AccountEntity a = new AccountEntity();
            a.setId(c.getId());
            a.setAccountNo(generateAccountNo());
            a.setType("WALLET");
            a.setStatus("ACTIVE");
            a.setCurrency(req.currency());
            a.setBalanceSnapshot(BigDecimal.ZERO);
            a.setVersion(0L);
            accountRepo.save(a);

            // 3) users
            UserEntity u = new UserEntity();
            u.setUsername(req.username());
            u.setPasswordHash(encoder.encode(req.password()));
            u.setRole("CUSTOMER");
            u.setCustomerId(c.getId());
            u = userRepo.save(u);

            String token = jwt.generateToken(u.getId(), u.getRole(), u.getCustomerId());
            return new TokenRes(token, "Bearer", u.getRole(), u.getCustomerId());

    }
        public TokenRes registerEmployee(RegisterEmployeeReq req) {
            if (userRepo.existsByUsername(req.username())) {
                throw new IllegalArgumentException("username already exists");
            }
            UserEntity u = new UserEntity();
            u.setUsername(req.username());
            u.setPasswordHash(encoder.encode(req.password()));
            u.setRole("EMPLOYEE");
            u.setCustomerId(null);
            u = userRepo.save(u);

            String token = jwt.generateToken(u.getId(), u.getRole(), null);
            return new TokenRes(token, "Bearer", u.getRole(), null);
        }

        public TokenRes login(LoginReq req) {
            UserEntity u = userRepo.findByUsername(req.username())
                    .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

            if (!encoder.matches(req.password(), u.getPasswordHash())) {
                throw new IllegalArgumentException("invalid credentials");
            }

            // enforce rule: CUSTOMER phải có customerId, EMPLOYEE phải null (optional)
            if ("CUSTOMER".equals(u.getRole()) && u.getCustomerId() == null) {
                throw new AccessDeniedException("customerId missing");
            }
            if ("EMPLOYEE".equals(u.getRole()) && u.getCustomerId() != null) {
                throw new AccessDeniedException("employee must not have customerId");
            }

            String token = jwt.generateToken(u.getId(), u.getRole(), u.getCustomerId());
            return new TokenRes(token, "Bearer", u.getRole(), u.getCustomerId());
        }

        private String generateAccountNo() {
            // <= 30 chars, unique enough cho demo
            return "AC" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
        }
    }
