package com.example.minicorebank.service;

import com.example.minicorebank.domain.Specificate.TransactionSpecs;
import com.example.minicorebank.dto.AccountTxItem;
import com.example.minicorebank.entity.TransactionEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReadTxService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public Page<AccountTxItem> listAccountTx(
            Long accountId,
            String direction,
            String type,
            String status,
            Instant from,
            Instant to,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String q,
            Pageable pageable
    ) {
        if (!accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        var spec = TransactionSpecs.forAccount(accountId)
                .and(TransactionSpecs.direction(accountId, direction))
                .and(TransactionSpecs.type(type))
                .and(TransactionSpecs.status(status))
                .and(TransactionSpecs.createdBetween(from, to))
                .and(TransactionSpecs.amountBetween(minAmount, maxAmount))
                .and(TransactionSpecs.search(q));

        return transactionRepository.findAll(spec, pageable)
                .map(tx -> toItem(accountId, tx));
    }

    private AccountTxItem toItem(Long accountId, TransactionEntity tx) {
        boolean isCredit = tx.getToAccount() != null
                && tx.getToAccount().getId() != null
                && tx.getToAccount().getId().equals(accountId);

        String dir = isCredit ? "CREDIT" : "DEBIT";

        Long counterpartyId = null;
        if (isCredit && tx.getFromAccount() != null) counterpartyId = tx.getFromAccount().getId();
        if (!isCredit && tx.getToAccount() != null) counterpartyId = tx.getToAccount().getId();

        return new AccountTxItem(
                tx.getId(),
                tx.getTxRef(),
                tx.getType(),
                tx.getStatus(),
                tx.getAmount(),
                tx.getCurrency(),
                dir,
                counterpartyId,
                tx.getNote(),
                tx.getCreatedAt()
        );
    }
}
