package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintime.fintime.Enums.FinanceGoalStatus;
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
    @JsonProperty("percent_goal")
    Double percentGoal;
    @NonNull
    Instant deadline;
    @NonNull
    FinanceGoalStatus status;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
}
