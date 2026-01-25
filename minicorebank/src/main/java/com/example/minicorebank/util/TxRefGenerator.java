package com.example.minicorebank.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TxRefGenerator {
    public String next() {
        // Ví dụ: TX-9F2A1B3C4D5E6F70
        return "TX-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }
}
