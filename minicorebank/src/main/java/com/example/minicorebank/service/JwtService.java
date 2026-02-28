package com.example.minicorebank.service;

import com.example.minicorebank.security.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessMillis;

    public  JwtService(JwtProperties props){
        byte[] bytes = Decoders.BASE64.decode(props.secret());
        this.key = Keys.hmacShaKeyFor(bytes);
        this.accessMillis = props.accessTokenMinutes() * 60_000L;
    }

    public String generateToken(Long userId, String role, Long customerId){
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessMillis);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("customerId", customerId)// null nếu EMPLOYEE
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }
    public Jws<Claims> parse(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
