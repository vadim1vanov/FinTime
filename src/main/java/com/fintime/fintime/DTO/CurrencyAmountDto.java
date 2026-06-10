package com.fintime.fintime.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CurrencyAmountDto {
    private String currency;
    private BigDecimal amount;
}
