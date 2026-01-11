package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
        @NotNull Long customerId
) {}
