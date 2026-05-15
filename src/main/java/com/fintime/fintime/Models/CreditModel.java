package com.fintime.fintime.Models;

import com.fintime.fintime.Enums.CreditStatus;
import com.fintime.fintime.Enums.CreditType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credits")
@Builder
public class CreditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;     // Сумма кредита
    @Column(name = "interest_rate")
    private BigDecimal interestRate;        // Процентная ставка
    @Column(name = "term_months")
    private Integer termMonths;             // Срок кредита в месяцах
    @Column(name ="remaining_balance")
    private BigDecimal remainingBalance;    //Остаток долга                 ВЫЧ.
    @Column(name = "paid_interest")
    private BigDecimal paidInterest;        // Оплаченные проценты          ВЫЧ.
    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;      // Дата последнего платежа
    @Column(name = "start_date")
    private LocalDate startDate;            // Дата выдачи кредита
    @Column(name = "end_date")
    private LocalDate endDate;              // Дата окончания кредита

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type")
    private CreditType type;                // Тип кредита
    @Enumerated(EnumType.STRING)
    private CreditStatus status;            // Статус кредита
    private String purpose;                 // Цель кредита
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
