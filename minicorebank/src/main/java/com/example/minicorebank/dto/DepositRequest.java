package com.example.minicorebank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequest(@NotNull Long accountId,
                             @JsonFormat(shape = JsonFormat.Shape.STRING)
                             @NotNull @Positive BigDecimal amount,
                             String currency,
                             String idempotencyKey,
                             String note) {

}
