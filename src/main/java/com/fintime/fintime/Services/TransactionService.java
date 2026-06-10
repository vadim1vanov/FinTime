package com.fintime.fintime.Services;


import com.fintime.fintime.DTO.TransactionDto;


import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAllTransactionsByAccount(Long accountId);
    TransactionDto getTransactionInfo(Long accountId, Long transactionId);
    void deleteTransaction(Long accountId, Long transactionId);
    void  deleteAllTransactionsByAccount(Long accountId);
    TransactionDto createIncomeTransaction(Long accountId, TransactionDto transactionDto);
    TransactionDto createTransferTransaction(Long accountId, Long accountTargetId, TransactionDto transactionDto);
    List<TransactionDto> getAllTransactions();
    void deleteAllTransactions();
    TransactionDto createExpenseTransaction(Long accountId, TransactionDto transactionDto);
    TransactionDto createExpenseTransaction(Long accountId, TransactionDto transactionDto, Long financeGoalId);
    TransactionDto createIncomeFinanceGoalTransaction(TransactionDto transactionDto, Long financeGoalId);
//    BigDecimal calculateTopUpSumLastMonth(Long accountId);
//    BigDecimal calculateExpenseSumLastMonth(Long accountId);
//    BigDecimal largestIncome(Long accountId);
//    BigDecimal largestExpense(Long accountId);
//    BigDecimal calculateTotalIncome(Long accountId);
//    BigDecimal calculateTotalExpense(Long accountId);

}
