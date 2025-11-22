package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinanceGoalDto {
    @NonNull
    Long id;
    @NonNull
    Long userId;
    @NonNull
    String name;
    @NonNull
    @JsonProperty("target_amount")
    BigDecimal targetAmount;
    @NonNull
    @JsonProperty("current_amount")
    BigDecimal currentAmount;
    @NonNull
    Instant deadline;
    @NonNull
    String status;
    @NonNull
    @JsonProperty("account_id")
    Long accountId;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
}
