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
public class AccountDto {
    @NonNull
    Long id;
    @JsonProperty("user_id")
    @NonNull
    Long userId;
    @NonNull
    @JsonProperty("account_name")
    String accountName;
    @NonNull
    String currency;
    @NonNull
    BigDecimal balance;
    @NonNull
    String status;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
    @NonNull
    @JsonProperty("account_position")
    private Integer accountPosition;
}
