package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.AccountInfoDto;
import com.fintime.fintime.DTO.BalanceHistoryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface AnalyticService {
    BigDecimal calculateAccountTopUpSumLastMonth(Long accountId);
    BigDecimal calculateAccountExpenseSumLastMonth(Long accountId);
    BigDecimal largestAccountIncome(Long accountId);
    BigDecimal largestAccountExpense(Long accountId);
    AccountInfoDto getAccountAnalytics(Long accountId);
    BigDecimal calculateTotalAccountIncome(Long accountId);
    BigDecimal calculateTotalAccountExpense(Long accountId);
    BigDecimal calculateTotalCreditAmount();
    BigDecimal calculateTotalDepositAmount();
    Map<String, BigDecimal> calculateTotalAccountsAmount();
    BigDecimal calculateTotalCreditsOverpayment();
    BigDecimal calculateTotalDepositProfit();
    List<BalanceHistoryDto> getBalanceHistory(Long accountId);
}
