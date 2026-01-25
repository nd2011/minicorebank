package com.example.minicorebank.repository;

import com.example.minicorebank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {
    Optional<TransactionEntity> findByTxRef(String txRef);
    Optional<TransactionEntity> findByIdempotencyKey(String idempotencyKey);
    boolean existsByReversedOfTxId(Long reversedOfTxId);

}
