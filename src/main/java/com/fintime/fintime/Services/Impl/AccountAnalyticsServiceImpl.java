package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.AccountInfoDto;
import com.fintime.fintime.Services.AccountAnalyticsService;
import com.fintime.fintime.Services.AccountService;
import com.fintime.fintime.Services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountAnalyticsServiceImpl implements AccountAnalyticsService {
    private final TransactionService transactionService;
    private final AccountService accountService;
    @Override
    public BigDecimal calculateTopUpSumLastMonth(Long accountId) {
        accountService.getAccountForCurrentUser(accountId);
        return transactionService.calculateTopUpSumLastMonth(accountId);
    }

    @Override
    public BigDecimal calculateExpenseSumLastMonth(Long accountId){
        accountService.getAccountForCurrentUser(accountId);
        return transactionService.calculateExpenseSumLastMonth(accountId);
    }
    @Override
    public BigDecimal largestIncome(Long accountId) {
        accountService.getAccountForCurrentUser(accountId); // проверка доступа
        return transactionService.largestIncome(accountId);
    }

    @Override
    public BigDecimal largestExpense(Long accountId) {
        accountService.getAccountForCurrentUser(accountId); // проверка доступа
        return transactionService.largestExpense(accountId);
    }

    @Override
    public AccountInfoDto getAccountAnalytics(Long accountId) {
        accountService.getAccountForCurrentUser(accountId);

        BigDecimal topUpSum  = transactionService.calculateTopUpSumLastMonth(accountId);
        BigDecimal expenseSum = transactionService.calculateExpenseSumLastMonth(accountId);
        BigDecimal net       = topUpSum.subtract(expenseSum);
        BigDecimal largestIncome = largestIncome(accountId);
        BigDecimal largestExpense = largestExpense(accountId);
        BigDecimal totalIncome     = transactionService.calculateTotalIncome(accountId);
        BigDecimal totalExpense    = transactionService.calculateTotalExpense(accountId);
        return AccountInfoDto.builder()
                .accountId(accountId)
                .topUpLastMonth(topUpSum)
                .expenseLastMonth(expenseSum)
                .netCashFlowLastMonth(net)
                .largestIncome(largestIncome)
                .largestExpense(largestExpense)
                .totalExpense(totalExpense)
                .totalIncome(totalIncome)
                .build();
    }

    @Override
    public BigDecimal totalIncome(Long accountId) {
        accountService.getAccountForCurrentUser(accountId);
        return transactionService.calculateTotalIncome(accountId);
    }

    @Override
    public BigDecimal totalExpense(Long accountId) {
        accountService.getAccountForCurrentUser(accountId);
        return transactionService.calculateTotalExpense(accountId);
    }
}
