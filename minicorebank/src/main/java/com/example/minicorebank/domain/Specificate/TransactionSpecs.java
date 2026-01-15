package com.example.minicorebank.domain.Specificate;

import com.example.minicorebank.entity.TransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecs {

    private static Specification<TransactionEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<TransactionEntity> forAccount(Long accountId) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("fromAccount").get("id"), accountId),
                cb.equal(root.get("toAccount").get("id"), accountId)
        );
    }

    public static Specification<TransactionEntity> direction(Long accountId, String dir) {
        if (dir == null || dir.isBlank()) return alwaysTrue();
        String d = dir.trim().toUpperCase();

        return (root, query, cb) -> {
            if ("CREDIT".equals(d)) return cb.equal(root.get("toAccount").get("id"), accountId);
            if ("DEBIT".equals(d)) return cb.equal(root.get("fromAccount").get("id"), accountId);
            return cb.conjunction();
        };
    }

    public static Specification<TransactionEntity> type(String type) {
        if (type == null || type.isBlank()) return alwaysTrue();
        return (root, query, cb) -> cb.equal(cb.upper(root.get("type")), type.trim().toUpperCase());
    }

    public static Specification<TransactionEntity> status(String status) {
        if (status == null || status.isBlank()) return alwaysTrue();
        return (root, query, cb) -> cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase());
    }

    public static Specification<TransactionEntity> createdBetween(Instant from, Instant to) {
        if (from == null && to == null) return alwaysTrue();

        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (from != null) ps.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            if (to != null) ps.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }

    public static Specification<TransactionEntity> amountBetween(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return alwaysTrue();

        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.greaterThanOrEqualTo(root.get("amount"), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(root.get("amount"), max));
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }

    public static Specification<TransactionEntity> search(String q) {
        if (q == null || q.isBlank()) return alwaysTrue();
        String like = "%" + q.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("txRef")), like),
                cb.like(cb.lower(root.get("note")), like)
        );
    }
}
