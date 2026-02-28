package com.example.minicorebank.dto;

public record TokenRes(
        String accessToken,
        String tokenType,
        String role,
        Long customerId
) {}
