package com.example.minicorebank.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "accounts")
public class AccountEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;

    @Column(name = "account_no", nullable = false, unique = true)
    private String accountNo;

    @Column(nullable = false)
    private String type;    //PAYMENT

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalanceSnapshot() {
        return balanceSnapshot;
    }

    public void setBalanceSnapshot(BigDecimal balanceSnapshot) {
        this.balanceSnapshot = balanceSnapshot;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Column(nullable = false)
    private String status;

    @Column(name = "balance_snap",nullable = false)
    private BigDecimal balanceSnapshot = BigDecimal.ZERO;

    @Version
    private  Long version;

    @Column(name = "created_at", nullable = false, updatable = false,insertable = false)
    private Instant createdAt;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(nullable = false)
    private String currency; // VND
}
