package com.example.minicorebank.dto;

public record AccountTxItem(
        Long id,
        String TxRef,
        String type,
        String status,
        java.math.BigDecimal amount,
        String currency,
        String direction,                                 // CREDIT/DEBIT theo góc nhìn account {id}
        Long counterpartyAccountId,                         // tài khoản đối ứng
        String note,
        java.time.Instant createdAt
) {
}
