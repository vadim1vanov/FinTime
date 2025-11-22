package com.fintime.fintime.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TransactionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    private BigDecimal amount;
    @Column(name = "transaction_type")
    private String transactionalType;
    private String description;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
