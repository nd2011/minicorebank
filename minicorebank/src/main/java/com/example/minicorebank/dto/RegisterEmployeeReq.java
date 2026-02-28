package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterEmployeeReq(
        @NotBlank String username,
        @NotBlank String password
) {}