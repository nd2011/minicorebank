package com.example.minicorebank.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(@NotBlank String fullName,
                                    String phone,
                                    String email) {

}
