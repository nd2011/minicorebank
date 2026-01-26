package com.example.minicorebank.util;


import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HoldRefGenerator {
    public String next(){
        return "HOLD-" + UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase();
    }
}
