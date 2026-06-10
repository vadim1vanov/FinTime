package com.fintime.fintime.Models;


import com.fintime.fintime.Enums.CapitalizationFrequency;
import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Enums.DepositTermType;
import com.fintime.fintime.Enums.ReplenishmentFrequency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "deposit")
public class DepositModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
//    @Column(name = "account_id")
//    private Long accountId;
    @Column(name = "deposit_name")
    private String depositName;
    @Column(name = "interest_rate")
    private BigDecimal interestRate;
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;
    @Column(name = "term_days")
    private Integer termDays;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private DepositStatus status;
//    @ManyToOne
//    @JoinColumn(name = "currency_id")
//    private Long currency;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "capitalization_frequency")
    @Enumerated(EnumType.STRING)
    private CapitalizationFrequency capitalizationFrequency;
    @Column(name = "replenishment_frequency")
    @Enumerated(EnumType.STRING)
    private ReplenishmentFrequency replenishmentFrequency;
    @Column(name = "deposit_term_type")
    @Enumerated(EnumType.STRING)
    private DepositTermType termType;
    @Column(name = "last_interest_accrual_date")
    private LocalDate lastInterestAccrualDate;
    @Column(name = "current_amount")
    private BigDecimal currentAmount;
    @Column(name = "next_interest_accrual_date")
    private LocalDate nextInterestAccrualDate;
}
