package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.AccountInfoDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface AccountAnalyticsService{
    BigDecimal calculateTopUpSumLastMonth(Long accountId);
    BigDecimal calculateExpenseSumLastMonth(Long accountId);
    BigDecimal largestIncome(Long accountId);
    BigDecimal largestExpense(Long accountId);
    AccountInfoDto getAccountAnalytics(Long accountId);
    BigDecimal totalIncome(Long accountId);
    BigDecimal totalExpense(Long accountId);
}
