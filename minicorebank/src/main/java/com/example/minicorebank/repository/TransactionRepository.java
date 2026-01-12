package com.example.minicorebank.repository;

import com.example.minicorebank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByTxRef(String txRef);
    Optional<TransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
