package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintime.fintime.Enums.CapitalizationFrequency;
import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Enums.DepositTermType;
import com.fintime.fintime.Enums.ReplenishmentFrequency;
import com.fintime.fintime.Models.CurrenciesModel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositDto {
    
    Long id;
    
    @JsonProperty("user_id")
    Long userId;
    
    @JsonProperty("deposit_name")
    String depositName;
    @JsonProperty("interest_rate")
    
    BigDecimal interestRate;
    
    @JsonProperty("principal_amount")
    BigDecimal principalAmount;
    
    @JsonProperty("term_days")
    Integer termDays;
    
    @JsonProperty("start_date")
    LocalDate startDate;
    
    @JsonProperty("end_date")
    LocalDate endDate;
    
    DepositStatus status;

//    @JsonProperty("currency_id")
//    Long currency;
    
    @JsonProperty("created_at")
    Instant createdAt;
    
    @JsonProperty("replenishment_frequency")
    ReplenishmentFrequency replenishmentFrequency;
    
    @JsonProperty("capitalization_frequency")
    CapitalizationFrequency capitalizationFrequency;
    
    @JsonProperty("deposit_term_type")
    DepositTermType termType;
    
    @JsonProperty("last_interest_accrual_date")
    LocalDate lastInterestAccrualDate;

    @JsonProperty("current_amount")
    BigDecimal currentAmount;

    @JsonProperty("next_interest_accrual_date")
    LocalDate nextInterestAccrualDate;
}
