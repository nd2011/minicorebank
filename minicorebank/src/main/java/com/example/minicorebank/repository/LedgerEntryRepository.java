package com.example.minicorebank.repository;

import com.example.minicorebank.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity,Long> {
}
