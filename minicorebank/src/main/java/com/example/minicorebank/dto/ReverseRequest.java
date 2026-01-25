package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReverseRequest (@NotNull  Long originalTxId,
                             @NotBlank String idempotencyKey,
                             String note){
}
