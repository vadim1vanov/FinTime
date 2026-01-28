package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintime.fintime.Enums.TransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDto {
    @NonNull
    Long id;
    @JsonProperty("account_id")
    Long accountId;
    @JsonProperty("account_target_id")
    Long accountTargetId;
    @JsonProperty("user_id")
    Long userId;
    @NonNull
    BigDecimal amount;
    @JsonProperty("finance_goal_id")
    Long financeGoalId;
    String description;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
    @JsonProperty("transaction_type")
    //    @NonNull
    TransactionType transactionType;
}
