package com.example.minicorebank.security;

public record AuthUser(Long userId, String role, Long customerId) {}
