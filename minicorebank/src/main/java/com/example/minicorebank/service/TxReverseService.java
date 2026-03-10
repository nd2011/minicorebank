package com.example.minicorebank.service;

import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.TransactionEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.TransactionRepository;
import com.example.minicorebank.util.ApiException;
import com.example.minicorebank.util.TxRefGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TxReverseService {

    private final TransactionRepository txRepo;
    private final AccountRepository accountRepo;
    private final TxRefGenerator txRefGenerator;

    public static final String HOUSE_ACCOUNT_NO = "HOUSE-000";

    @Transactional
    public TransactionEntity reverse(Long originalTxId, String idempotencyKey, String note) {

        // 1) Idempotency
        var existing = txRepo.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) return existing.get();

        // 2) Load original tx
        TransactionEntity original = txRepo.findById(originalTxId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Original tx not found"));

        // 3) Validate status
        if (!"POSTED".equals(original.getStatus())) {
            throw new ApiException("TX_NOT_SUCCESS", "Only POSTED tx can be reversed");
        }

        // 4) Prevent double reverse
        if (txRepo.existsByReversedOfTxId(originalTxId)) {
            throw new ApiException("TX_ALREADY_REVERSED", "Transaction already reversed");
        }

        AccountEntity from;
        AccountEntity to;

        // 5) Determine reversal direction (LOCK accounts)
        switch (original.getType()) {
            case "TRANSFER" -> {
                from = lockById(original.getToAccount().getId());   // đảo chiều
                to   = lockById(original.getFromAccount().getId());
            }
            case "DEPOSIT" -> {
                from = lockById(original.getToAccount().getId());   // customer -> HOUSE
                to   = houseAccountForUpdate();                     // HOUSE lock
            }
            case "WITHDRAW" -> {
                from = houseAccountForUpdate();                     // HOUSE lock
                to   = lockById(original.getFromAccount().getId()); // HOUSE -> customer
            }
            default -> throw new ApiException("TX_TYPE_NOT_REVERSIBLE", "Unsupported tx type: " + original.getType());
        }

        // 6) Balance check (nếu from không phải HOUSE)
        if (!isHouse(from) && from.availableSnapshot().compareTo(original.getAmount()) < 0) {
            throw new ApiException("INSUFFICIENT_FUNDS", "Not enough balance to reverse");
        }

        // 7) Apply balances
        from.debit(original.getAmount());
        to.credit(original.getAmount());
        accountRepo.save(from);
        accountRepo.save(to);

        // 8) Create reversal tx
        TransactionEntity rev = new TransactionEntity();
        rev.setTxRef(txRefGenerator.next());
        rev.setType("REVERSAL");
        rev.setStatus("POSTED");
        rev.setAmount(original.getAmount());
        rev.setCurrency(original.getCurrency());
        rev.setFromAccount(from);
        rev.setToAccount(to);
        rev.setIdempotencyKey(idempotencyKey);
        rev.setNote(note);
        rev.setReversedOfTxId(original.getId());

        return txRepo.save(rev);
    }

    private AccountEntity lockById(Long id) {
        return accountRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new ApiException("ACCOUNT_NOT_FOUND", "Account not found: " + id));
    }

    private boolean isHouse(AccountEntity a) {
        return HOUSE_ACCOUNT_NO.equals(a.getAccountNo());
    }

    private AccountEntity houseAccountForUpdate() {
        return accountRepo.findByAccountNoForUpdate(HOUSE_ACCOUNT_NO)
                .orElseThrow(() -> new ApiException("HOUSE_ACCOUNT_NOT_FOUND", "HOUSE-000 missing"));
    }
}