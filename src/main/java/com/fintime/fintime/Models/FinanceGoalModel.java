package com.fintime.fintime.Models;


import com.fintime.fintime.Enums.FinanceGoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;


@Table(name = "finance_goals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
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
    @Enumerated(EnumType.STRING)
    private FinanceGoalStatus status;
    @Column(name = "percent_goal")
    private Double percentGoal;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
