package com.fintime.fintime.Models;

import com.fintime.fintime.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;


@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class TransactionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "account_target_id")
    private Long accountTargetId;
    private BigDecimal amount;
    @Column(name = "finance_goal_id")
    private Long financeGoalId;
    private String description;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "balance_after")
    private BigDecimal balanceAfter;

}
