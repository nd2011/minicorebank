package com.example.minicorebank.service;

import com.example.minicorebank.dto.FreezeRequest;
import com.example.minicorebank.dto.UnfreezeRequest;
import com.example.minicorebank.entity.AccountEntity;
import com.example.minicorebank.entity.HoldEntity;
import com.example.minicorebank.repository.AccountRepository;
import com.example.minicorebank.repository.HoldRepository;
import com.example.minicorebank.util.ApiException;
import com.example.minicorebank.util.HoldRefGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class HoldService {

    private final AccountRepository accountRepo;
    private final HoldRepository holdRepo;
    private final HoldRefGenerator holdRefGenerator;

    @Transactional
    public HoldEntity freeze(FreezeRequest req){
        var existing = holdRepo.findByIdempotencyKey(req.idempotencyKey());
        if (existing.isPresent()) return existing.get();

        AccountEntity acc = accountRepo.findById(req.accountId())
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Account not found"));

        if (!acc.getCurrency().equals(req.currency())){
            throw new ApiException("CURRENCY_MISMATCH", "Account currency mismatch");
        }
        if (acc.availableSnapshot().compareTo(req.amount())<0){
            throw new ApiException("INSUFFICIENT_AVAILABLE", "Not enough available balance to freeze");
        }

        acc.freeze(req.amount());
        accountRepo.save(acc);

        HoldEntity hold = new HoldEntity();
        hold.setHoldRef(holdRefGenerator.next());
        hold.setAccount(acc);
        hold.setAmount(req.amount());
        hold.setCurrency(req.currency());
        hold.setStatus("HELD");
        hold.setIdempotencyKey(req.idempotencyKey());
        hold.setNote(req.note());

        return holdRepo.save(hold);
    }

    @Transactional
    public HoldEntity unfreeze(UnfreezeRequest req){
        // idempotency
        var existing = holdRepo.findByIdempotencyKey(req.idempotencyKey());
        if (existing.isPresent()) return existing.get();

        HoldEntity hold = holdRepo.findByHoldRef(req.holdRef())
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Hold not found"));

        if (!"HELD".equals(hold.getStatus())) {
            throw new ApiException("HOLD_NOT_ACTIVE", "Hold is not active");
        }

        AccountEntity acc = hold.getAccount();

        acc.unfreeze(hold.getAmount());
        accountRepo.save(acc);


        hold.setStatus("RELEASED");
        hold.setReleasedAt(Instant.now());
        if (req.note() != null && !req.note().isBlank()){
            hold.setNote(req.note());
        }

        // để đảm bảo retry trả cùng kết quả, lưu idempotencyKey cho lần unfreeze
        hold.setIdempotencyKey(req.idempotencyKey());

return holdRepo.save(hold);    }
}
