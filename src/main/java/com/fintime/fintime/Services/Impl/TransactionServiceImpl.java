package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Enums.TransactionType;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.AccountDtoFactory;
import com.fintime.fintime.Factories.TransactionDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Models.FinanceGoalModel;
import com.fintime.fintime.Models.TransactionModel;
import com.fintime.fintime.Repository.AccountRepository;
import com.fintime.fintime.Repository.TransactionRepository;
import com.fintime.fintime.Services.*;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final FinanceGoalService financeGoalService;
    private final TransactionCategoryService transactionCategoryService;

    @Override
    public List<TransactionDto> getAllTransactionsByAccount(Long accountId) {
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        return transactionRepository.findAll().stream()
                .map(TransactionDtoFactory::makeTransactionDto)
                .filter(transactionDto -> Objects.equals(transactionDto.getAccountId(), account.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto createIncomeTransaction(Long accountId, TransactionDto transactionDto){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        accountService.increaseAccountBalance(accountId, transactionDto.getAmount());
        TransactionModel transaction = transactionRepository.saveAndFlush(
                TransactionModel.builder()
                        .amount(transactionDto.getAmount())
                        .accountId(accountId)
                        .transactionType(TransactionType.INCOME)
                        .userId(account.getUserId())
                        .description(transactionDto.getDescription())
                        .createdAt(Instant.now())
                        .categoryName(transactionDto.getCategoryName())
                        .build()
        );
        return TransactionDtoFactory.makeTransactionDto(transaction);
    }

    @Override
    public TransactionDto getTransactionInfo(Long accountId, Long transactionId){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        return transactionRepository.findById(transactionId)
                .filter(transaction -> Objects.equals(account.getId(), transaction.getAccountId()))
                .map(TransactionDtoFactory::makeTransactionDto)
                .orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }

    @Override
    public void deleteTransaction(Long accountId, Long transactionId){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        TransactionModel transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new NotFoundException("Transaction not found!")
        );
        if(!transaction.getAccountId().equals(accountId)){
            throw new AccessDeniedException("You don't have access!");
        }
        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional
    public void deleteAllTransactionsByAccount(Long accountId){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        transactionRepository.deleteAllByAccountId(accountId);
    }

    @Override
    @Transactional
    public TransactionDto createExpenseTransaction(Long accountId, TransactionDto transactionDto, Long financeGoalId){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        accountService.decreaseAccountBalance(accountId, transactionDto.getAmount());
        TransactionModel transactional = transactionRepository.saveAndFlush(
                TransactionModel.builder()
                        .amount(transactionDto.getAmount())
                        .transactionType(TransactionType.EXPENSE_FINANCE_GOAL)
                        .description(transactionDto.getDescription())
                        .userId(account.getUserId())
                        .createdAt(Instant.now())
                        .financeGoalId(financeGoalId)
                        .accountId(accountId)
                        .build()
        );
        return TransactionDtoFactory.makeTransactionDto(transactional);
    }

    @Override
    @Transactional
    public TransactionDto createExpenseTransaction(Long accountId, TransactionDto transactionDto){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        accountService.decreaseAccountBalance(accountId, transactionDto.getAmount());
        TransactionModel transactional = transactionRepository.saveAndFlush(
                TransactionModel.builder()
                        .amount(transactionDto.getAmount())
                        .transactionType(TransactionType.EXPENSE)
                        .description(transactionDto.getDescription())
                        .userId(account.getUserId())
                        .createdAt(Instant.now())
                        .financeGoalId(transactionDto.getFinanceGoalId())
                        .accountId(accountId)
                        .build()
        );
        return TransactionDtoFactory.makeTransactionDto(transactional);
    }

    @Override
    public TransactionDto createTransferTransaction(Long accountId, Long targetAccountId, TransactionDto transactionDto){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        AccountModel targetAccount = accountService.getAccountForCurrentUser(targetAccountId);
        accountService.decreaseAccountBalance(accountId, transactionDto.getAmount());
        accountService.increaseAccountBalance(targetAccountId, transactionDto.getAmount());
        TransactionModel transactional = transactionRepository.saveAndFlush(
                TransactionModel.builder()
                        .transactionType(TransactionType.TRANSFER)
                        .description(transactionDto.getDescription())
                        .createdAt(Instant.now())
                        .userId(account.getUserId())
                        .amount(transactionDto.getAmount())
                        .accountId(accountId)
                        .accountTargetId(targetAccountId)
                        .build()
        );
        return TransactionDtoFactory.makeTransactionDto(transactional);
    }

    @Override
    public List<TransactionDto> getAllTransactions(){
        Long currentUserId = userService.getCurrentUserId();
        return transactionRepository.findAll().stream()
                .map(TransactionDtoFactory::makeTransactionDto)
                .filter(transaction -> transaction.getUserId().equals(currentUserId))
                .toList();
    }

    @Override
    public void deleteAllTransactions(){
        Long currentUserId = userService.getCurrentUserId();
        List<TransactionModel> transactions = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getUserId().equals(currentUserId))
                .toList();
        transactionRepository.deleteAll(transactions);
    }

    @Override
    public TransactionDto createIncomeFinanceGoalTransaction(TransactionDto transactionDto, Long financeGoalId){
        FinanceGoalModel financeGoalModel = financeGoalService.getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalService.increaseFinanceGoal(financeGoalId, transactionDto.getAmount());
        TransactionModel transactionModel = transactionRepository.saveAndFlush(
                TransactionModel.builder()
                        .transactionType(TransactionType.INCOME_FINANCE_GOAL)
                        .description(transactionDto.getDescription())
                        .amount(transactionDto.getAmount())
                        .financeGoalId(financeGoalId)
                        .userId(financeGoalModel.getUserId())
                        .createdAt(Instant.now())
                        .build()
        );
        return TransactionDtoFactory.makeTransactionDto(transactionModel);
    }


    @Override
    public BigDecimal calculateTopUpSumLastMonth(Long accountId) {
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
    public BigDecimal calculateExpenseSumLastMonth(Long accountId) {
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
    public BigDecimal largestIncome(Long accountId) {
        Optional<BigDecimal> max = transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(TransactionModel::getAmount)
                .max(BigDecimal::compareTo);

        return max.orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal largestExpense(Long accountId) {
        Optional<BigDecimal> max = transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(TransactionModel::getAmount)
                .max(BigDecimal::compareTo);

        return max.orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalIncome(Long accountId) {
        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalExpense(Long accountId) {
        return transactionRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getAccountId(), accountId))
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }



}
