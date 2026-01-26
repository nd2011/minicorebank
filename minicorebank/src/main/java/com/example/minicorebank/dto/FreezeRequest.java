package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FreezeRequest(
        @NotNull Long accountId,
        @NotNull BigDecimal amount,
        @NotNull String currency,
        @NotNull String idempotencyKey,
        String note
        ) {
}
