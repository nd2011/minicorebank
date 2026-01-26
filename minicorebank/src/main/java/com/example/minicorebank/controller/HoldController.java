package com.example.minicorebank.controller;

import com.example.minicorebank.dto.FreezeRequest;
import com.example.minicorebank.dto.UnfreezeRequest;
import com.example.minicorebank.entity.HoldEntity;
import com.example.minicorebank.service.HoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holds")
public class HoldController {

    private final HoldService holdService;

    @PostMapping("/freeze")
    public HoldEntity freeze(@Valid @RequestBody FreezeRequest req) {
        return holdService.freeze(req);
    }

    @PostMapping("/unfreeze")
    public HoldEntity unfreeze(@Valid @RequestBody UnfreezeRequest req) {
        return holdService.unfreeze(req);
    }
}
