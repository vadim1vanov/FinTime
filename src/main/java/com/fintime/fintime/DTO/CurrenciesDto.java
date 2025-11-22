package com.fintime.fintime.DTO;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrenciesDto {
    @NonNull
    Long id;
    @NonNull
    String code;
    @NonNull
    String name;
    @NonNull
    String symbol;
}
