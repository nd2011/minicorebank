package com.example.minicorebank.controller;

import com.example.minicorebank.dto.LoginReq;
import com.example.minicorebank.dto.RegisterCustomerReq;
import com.example.minicorebank.dto.RegisterEmployeeReq;
import com.example.minicorebank.dto.TokenRes;
import com.example.minicorebank.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register/customer")
    public TokenRes registerCustomer(@RequestBody RegisterCustomerReq req) {
        return auth.registerCustomer(req);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/register/employee")
    public TokenRes registerEmployee(@RequestBody RegisterEmployeeReq req) {
        return auth.registerEmployee(req);
    }

    @PostMapping("/login")
    public TokenRes login(@RequestBody LoginReq req) {
        return auth.login(req);
    }
}