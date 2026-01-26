package com.example.minicorebank.repository;

import com.example.minicorebank.entity.HoldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoldRepository extends JpaRepository<HoldEntity, Long> {
    Optional<HoldEntity> findByIdempotencyKey(String idempotencyKey);
    Optional<HoldEntity> findByHoldRef(String holdRef);
}
