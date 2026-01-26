package com.example.minicorebank.service;

import com.example.minicorebank.dto.DepositRequest;
import com.example.minicorebank.dto.TransferRequest;
import com.example.minicorebank.entity.*;
import com.example.minicorebank.dto.WithdrawRequest;
import com.example.minicorebank.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostingService {

    private static final String HOUSE_ACCOUNT_NO = "HOUSE-000";
    private static final String DEFAULT_CCY = "VND";

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public TransactionEntity deposit(DepositRequest req) {
        if (req.idempotencyKey() != null) {
            var existed = transactionRepository.findByIdempotencyKey(req.idempotencyKey());
            if (existed.isPresent()) return existed.get();
        }

        String ccy = (req.currency() == null || req.currency().isBlank()) ? DEFAULT_CCY : req.currency().trim();

        AccountEntity to = accountRepository.findByIdForUpdate(req.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + req.accountId()));
        AccountEntity house = accountRepository.findByAccountNoForUpdate(HOUSE_ACCOUNT_NO)
                .orElseThrow(() -> new IllegalStateException("Missing house account. Run Flyway V3 seed."));

        assertActive(to);
        assertCurrency(to, ccy);

        BigDecimal amount = req.amount();

        TransactionEntity tx = new TransactionEntity();
        tx.setTxRef(genTxRef());
        tx.setType("DEPOSIT");
        tx.setStatus("POSTED");
        tx.setAmount(amount);
        tx.setCurrency(ccy);
        tx.setFromAccount(house);
        tx.setToAccount(to);
        tx.setIdempotencyKey(req.idempotencyKey());
        tx.setNote(req.note());
        tx = transactionRepository.save(tx);

        // Balance rule: CREDIT => +, DEBIT => -
        applyCredit(to, amount);
        applyDebit(house, amount);

        accountRepository.save(to);
        accountRepository.save(house);

        ledgerEntryRepository.save(newLedger(tx, to, "CREDIT", amount, ccy));
        ledgerEntryRepository.save(newLedger(tx, house, "DEBIT", amount, ccy));

        return tx;
    }

    @Transactional
    public TransactionEntity transfer(TransferRequest req) {
        if (req.idempotencyKey() != null) {
            var existed = transactionRepository.findByIdempotencyKey(req.idempotencyKey());
            if (existed.isPresent()) return existed.get();
        }

        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new IllegalArgumentException("fromAccountId must be different from toAccountId");
        }

        String ccy = (req.currency() == null || req.currency().isBlank()) ? DEFAULT_CCY : req.currency().trim();
        BigDecimal amount = req.amount();

        // lock theo thứ tự để giảm deadlock
        Long a = Math.min(req.fromAccountId(), req.toAccountId());
        Long b = Math.max(req.fromAccountId(), req.toAccountId());

        AccountEntity first = accountRepository.findByIdForUpdate(a)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + a));
        AccountEntity second = accountRepository.findByIdForUpdate(b)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + b));

        AccountEntity from = req.fromAccountId().equals(first.getId()) ? first : second;
        AccountEntity to = req.toAccountId().equals(first.getId()) ? first : second;

        assertActive(from);
        assertActive(to);
        assertCurrency(from, ccy);
        assertCurrency(to, ccy);

        if (from.availableSnapshot().compareTo(req.amount())<0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        TransactionEntity tx = new TransactionEntity();
        tx.setTxRef(genTxRef());
        tx.setType("TRANSFER");
        tx.setStatus("POSTED");
        tx.setAmount(amount);
        tx.setCurrency(ccy);
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setIdempotencyKey(req.idempotencyKey());
        tx.setNote(req.note());
        tx = transactionRepository.save(tx);

        applyDebit(from, amount);
        applyCredit(to, amount);

        accountRepository.save(from);
        accountRepository.save(to);

        ledgerEntryRepository.save(newLedger(tx, from, "DEBIT", amount, ccy));
        ledgerEntryRepository.save(newLedger(tx, to, "CREDIT", amount, ccy));

        return tx;
    }

    private static String genTxRef() {
        return "TX-" + UUID.randomUUID().toString().replace("-", "");
    }

    private static void assertActive(AccountEntity a) {
        if (!"ACTIVE".equalsIgnoreCase(a.getStatus())) {
            throw new IllegalArgumentException("Account is not ACTIVE: " + a.getId());
        }
    }

    private static void assertCurrency(AccountEntity a, String ccy) {
        if (!ccy.equalsIgnoreCase(a.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch: account=" + a.getCurrency() + ", req=" + ccy);
        }
    }

    private static void applyCredit(AccountEntity a, BigDecimal amt) {
        a.setBalanceSnapshot(a.getBalanceSnapshot().add(amt));
    }

    private static void applyDebit(AccountEntity a, BigDecimal amt) {
        a.setBalanceSnapshot(a.getBalanceSnapshot().subtract(amt));
    }

    private static LedgerEntryEntity newLedger(TransactionEntity tx, AccountEntity acc, String dir, BigDecimal amt, String ccy) {
        LedgerEntryEntity e = new LedgerEntryEntity();
        e.setTransaction(tx);
        e.setAccount(acc);
        e.setDirection(dir);
        e.setAmount(amt);
        e.setCurrency(ccy);
        return e;
    }
    @Transactional
    public TransactionEntity withdraw(WithdrawRequest req){
        if(req.idempotencuKey() !=null){
            var existed = transactionRepository.findByIdempotencyKey(req.idempotencuKey());
            if (existed.isPresent()) return existed.get();
        }

        String ccy = (req.currency() == null || req.currency().isBlank() ? DEFAULT_CCY : req.currency().trim());

        AccountEntity from = accountRepository.findByIdForUpdate(req.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + req.accountId()));
        AccountEntity house = accountRepository.findByAccountNoForUpdate(HOUSE_ACCOUNT_NO)
                .orElseThrow(() -> new IllegalStateException("Missing house account. Run Flyway V3 seed."));

        assertActive(from);
        assertCurrency(from, ccy);

        BigDecimal amount = req.amount();
        if (from.availableSnapshot().compareTo(req.amount()) < 0){
            throw new IllegalArgumentException("Insufficient balance");
        }

        TransactionEntity tx = new TransactionEntity();
        tx.setTxRef(genTxRef());
        tx.setType("WITHDRAW");
        tx.setStatus("POSTED");
        tx.setAmount(amount);
        tx.setCurrency(ccy);
        tx.setFromAccount(from);
        tx.setToAccount(house);
        tx.setIdempotencyKey(req.idempotencuKey());
        tx.setNote(req.note());
        tx = transactionRepository.save(tx);

        // Withdraw: customer giảm tiền (DEBIT), house tăng tiền (CREDIT)
        applyDebit(from, amount);
        applyCredit(house, amount);

        accountRepository.save(from);
        accountRepository.save(house);

        ledgerEntryRepository.save(newLedger(tx, from, "DEBIT", amount, ccy));
        ledgerEntryRepository.save(newLedger(tx, house, "CREDIT", amount, ccy));

        return tx;
    }


}
