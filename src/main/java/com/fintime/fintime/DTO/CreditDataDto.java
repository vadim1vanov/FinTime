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
public class CreditDataDto {
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
    @JsonProperty("last_payment_date")
    LocalDate lastPaymentDate;
    @JsonProperty("paid_interest")
    BigDecimal paidInterest;
    @JsonProperty("next_payment_date")
    LocalDate nextPaymentDate;
    @JsonProperty("remaining_balance")
    BigDecimal remainingBalance;


    @JsonProperty("start_date")
    LocalDate startDate;
    @JsonProperty("end_date")
    LocalDate endDate;
    BigDecimal progress;
    @JsonProperty("credit_type")
    CreditType type;
    CreditStatus status;
    String purpose;
    @JsonProperty("created_at")
    Instant createdAt;
}
