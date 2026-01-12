package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequest(@NotNull Long accountId,
                             @NotNull @Positive BigDecimal amount,
                             String currency,
                             String idempotencyKey,
                             String note) {

}
