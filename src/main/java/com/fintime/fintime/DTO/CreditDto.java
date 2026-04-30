package com.fintime.fintime.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintime.fintime.Enums.CreditStatus;
import com.fintime.fintime.Enums.CreditType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreditDto {
    @NonNull
    Long id;
    @JsonProperty("account_id")
    Long accountId;
    @JsonProperty("user_id")
    Long userId;
    @JsonProperty("principal_amount")
    BigDecimal  principalAmount;
    @JsonProperty("interest_rate")
    BigDecimal interestRate;
    @JsonProperty("term_months")
    Integer termMonths;
    @JsonProperty("monthly_payment")
    BigDecimal monthlyPayment;
    @JsonProperty("remaining_balance")
    BigDecimal remainingBalance;
    @JsonProperty("accrued_interest")
    BigDecimal accruedInterest;
    @JsonProperty("penalty_amount")
    BigDecimal penaltyAmount;
    @JsonProperty("start_date")
    LocalDate startDate;
    @JsonProperty("end_date")
    LocalDate endDate;
    @JsonProperty("next_payment_date")
    LocalDate nextPaymentDate;
    @JsonProperty("credit_type")
    CreditType type;
    CreditStatus status;
    String purpose;
    @JsonProperty("created_at")
    Instant createdAt;
}
