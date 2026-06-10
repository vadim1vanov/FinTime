package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.*;
import com.fintime.fintime.Enums.CreditStatus;
import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Enums.TransactionType;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Models.TransactionModel;
import com.fintime.fintime.Repository.TransactionRepository;
import com.fintime.fintime.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CreditService creditService;
    private final DepositService depositService;
    private final TransactionService transactionService;
    @Override
    public BigDecimal calculateAccountTopUpSumLastMonth(Long accountId) {
        Instant from = Instant.now().minus(Duration.ofDays(30));
        Instant to = Instant.now();

        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .filter(t -> t.getCreatedAt() != null)
                .filter(t -> !t.getCreatedAt().isBefore(from))
                .filter(t -> t.getCreatedAt().isBefore(to))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateAccountExpenseSumLastMonth(Long accountId) {
        Instant from = Instant.now().minus(Duration.ofDays(30));
        Instant to   = Instant.now();

        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .filter(t -> t.getCreatedAt() != null)
                .filter(t -> !t.getCreatedAt().isBefore(from))
                .filter(t -> t.getCreatedAt().isBefore(to))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal largestAccountIncome(Long accountId) {
        Optional<BigDecimal> max = transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(TransactionModel::getAmount)
                .max(BigDecimal::compareTo);

        return max.orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal largestAccountExpense(Long accountId) {
        Optional<BigDecimal> max = transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(TransactionModel::getAmount)
                .max(BigDecimal::compareTo);

        return max.orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalAccountIncome(Long accountId) {
        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalAccountExpense(Long accountId) {
        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public AccountInfoDto getAccountAnalytics(Long accountId) {
        accountService.getAccountForCurrentUser(accountId);

        BigDecimal topUpSum  = calculateAccountTopUpSumLastMonth(accountId);
        BigDecimal expenseSum = calculateAccountExpenseSumLastMonth(accountId);
        BigDecimal net       = topUpSum.subtract(expenseSum);
        BigDecimal largestIncome = largestAccountIncome(accountId);
        BigDecimal largestExpense = largestAccountExpense(accountId);
        BigDecimal totalIncome     = calculateTotalAccountIncome(accountId);
        BigDecimal totalExpense    = calculateTotalAccountExpense(accountId);
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
    public BigDecimal calculateTotalCreditAmount() {
        return creditService.getAllCredits().stream()
                .filter(credit -> CreditStatus.ACTIVE.equals(credit.getStatus()))
                .map(CreditDataDto::getRemainingBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, BigDecimal> calculateTotalAccountsAmount() {
        return accountService.getAllAccounts().stream()
                .filter(account -> Objects.equals(account.getStatus(), "active"))
                .filter(account -> account.getBalance() != null)
                .collect(Collectors.groupingBy(
                        AccountDto::getCurrency,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                AccountDto::getBalance,
                                BigDecimal::add
                        )
                ));
    }

    @Override
    public BigDecimal calculateTotalDepositAmount(){
        return depositService.getAllDeposits().stream()
                .filter(deposit -> DepositStatus.ACTIVE.equals(deposit.getStatus()))
                .map(DepositDto::getPrincipalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    @Override
    public BigDecimal calculateTotalCreditsOverpayment() {
        return creditService.getAllCredits().stream()
                .filter(c -> CreditStatus.ACTIVE.equals(c.getStatus()))
                .map(c -> creditService.calculateOverpayment(
                        c.getPrincipalAmount(),
                        c.getInterestRate(),
                        c.getTermMonths()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalDepositProfit() {
        return depositService.getAllDeposits().stream()
                .filter(deposit -> DepositStatus.ACTIVE.equals(deposit.getStatus()))
                .map(deposit -> depositService.calculateProfit(
                        deposit.getPrincipalAmount(),
                        deposit.getInterestRate(),
                        deposit.getTermDays(),
                        deposit.getCapitalizationFrequency()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    @Override
    public List<BalanceHistoryDto> getBalanceHistory(Long accountId) {

        accountService.getAccountForCurrentUser(accountId);

        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(30);

        List<TransactionDto> transactions =
                transactionService.getAllTransactionsByAccount(accountId);

        Map<LocalDate, BigDecimal> balancesByDay =
                transactions.stream()
                        .filter(t -> t.getBalanceAfter() != null)
                        .collect(Collectors.toMap(
                                t -> t.getCreatedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate(),
                                TransactionDto::getBalanceAfter,
                                (oldVal, newVal) -> newVal
                        ));

        List<BalanceHistoryDto> result = new ArrayList<>();

        BigDecimal lastKnownBalance = BigDecimal.ZERO;

        for (LocalDate day = fromDate;
             !day.isAfter(today);
             day = day.plusDays(1)) {

            if (balancesByDay.containsKey(day)) {
                lastKnownBalance = balancesByDay.get(day);
            }

            result.add(
                    BalanceHistoryDto.builder()
                            .date(day)
                            .balance(lastKnownBalance)
                            .build()
            );
        }

        return result;
    }

}
