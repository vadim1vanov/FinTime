package com.fintime.fintime.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Table(name = "goals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class FinanceGoalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String name;
    @Column(name = "target_amount")
    private BigDecimal targetAmount;
    @Column(name = "current_amount")
    private BigDecimal currentAmount;
    private Instant deadline;
    private String status;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
