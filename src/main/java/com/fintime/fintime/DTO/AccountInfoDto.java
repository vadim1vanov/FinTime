package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountInfoDto {
    @JsonProperty("account_id")
    Long accountId;
    @JsonProperty("top_up_last_month")
    BigDecimal topUpLastMonth;
    @JsonProperty("total_expense")
    BigDecimal totalExpense;
    @JsonProperty("total_income")
    BigDecimal totalIncome;
    @JsonProperty("expense_last_month")
    BigDecimal expenseLastMonth;

    @JsonProperty("net_cash_flow_last_month")
    BigDecimal netCashFlowLastMonth;
    @JsonProperty("largest_income")
    BigDecimal largestIncome;
    @JsonProperty("largest_expense")
    BigDecimal largestExpense;
}
