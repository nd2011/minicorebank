package com.example.minicorebank.controller;

import com.example.minicorebank.dto.DepositRequest;
import com.example.minicorebank.dto.ReverseRequest;
import com.example.minicorebank.dto.TransferRequest;
import com.example.minicorebank.dto.WithdrawRequest;
import com.example.minicorebank.entity.TransactionEntity;
import com.example.minicorebank.service.PostingService;
import com.example.minicorebank.service.TxReverseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tx")
public class TxController {

    private final PostingService postingService;
    private final TxReverseService txReverseService;


    @PostMapping("/deposit")
    public TransactionEntity deposit(@Valid @RequestBody DepositRequest req) {
        return postingService.deposit(req);
    }

    @PostMapping("/transfer")
    public TransactionEntity transfer(@Valid @RequestBody TransferRequest req) {
        return postingService.transfer(req);
    }
    @PostMapping("/withdraw")
    public TransactionEntity withdraw(@Valid @RequestBody WithdrawRequest req){
        return postingService.withdraw(req);
    }
    @PostMapping("/reverse")
    public TransactionEntity reverse(@Valid @RequestBody ReverseRequest req) {
        return txReverseService.reverse(req.originalTxId(), req.idempotencyKey(), req.note());
    }


}
