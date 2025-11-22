package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    @NonNull
    Long accountId;
    @NonNull
    BigDecimal amount;
    @JsonProperty("transaction_type")
    @NonNull
    String transactionalType;
    @NonNull
    String description;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
}
