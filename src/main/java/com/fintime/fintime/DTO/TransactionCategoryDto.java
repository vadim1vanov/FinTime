package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintime.fintime.Enums.TransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionCategoryDto {
    @NonNull
    Long id;
    @JsonProperty("user_id")
    Long userId;
    @JsonProperty("transaction_type")
    @NonNull
    TransactionType transactionType;
    @JsonProperty("category_name")
    @NonNull
    String categoryName;
}
