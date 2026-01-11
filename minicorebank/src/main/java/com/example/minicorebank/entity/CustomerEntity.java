package com.example.minicorebank.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "customers")
public class CustomerEntity {
    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name",  nullable = false)
    private String fullName;

    private String phone;
    private String email;

    @Column(nullable = false)
    private  String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "created_at", nullable = false, updatable = false,insertable = false)
    private Instant createdAt;
}
