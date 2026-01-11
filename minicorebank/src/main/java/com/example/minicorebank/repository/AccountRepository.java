package com.example.minicorebank.repository;

import com.example.minicorebank.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    boolean existsByAccountNo(String accountNo);
}
