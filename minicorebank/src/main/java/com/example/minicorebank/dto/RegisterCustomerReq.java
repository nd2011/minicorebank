package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterCustomerReq(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String fullName,
        String phone,
        String email,
        @NotBlank String currency
) {}
