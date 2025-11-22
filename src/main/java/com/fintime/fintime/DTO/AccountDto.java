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
    Long id;
    @JsonProperty("user_id")
    Long userId;
    @JsonProperty("account_name")
    String accountName;
    String currency;
    BigDecimal balance;
    String status;
    @JsonProperty("created_at")
    Instant createdAt;
}
