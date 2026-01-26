package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotBlank;

public record UnfreezeRequest(
        @NotBlank String holdRef,
        @NotBlank String idempotencyKey,
        String note
) {
}
