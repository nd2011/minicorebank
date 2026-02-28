package com.example.minicorebank.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {
    public static AuthUser require() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof AuthUser au)) {
            throw new RuntimeException("Unauthenticated");
        }
        return au;
    }
}