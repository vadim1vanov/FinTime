package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.TransactionCategoryDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Models.TransactionCategoryModel;

import java.util.List;

public interface TransactionCategoryService {
    TransactionCategoryDto createTransactionCategory(TransactionCategoryDto transactionCategoryDto);
    void editTransactionCategory(Long transactionCategoryId, TransactionCategoryDto updatedTransactionCategoryDto);
    List<TransactionCategoryDto> getIncomeTransactionCategories();
    List<TransactionCategoryDto> getIncomeTransactionCategories(Long userId);
//    List<TransactionDto> getExpenseTransactionCategories();
//    List<TransactionDto> getOtherTransactionCategories();
    TransactionCategoryModel getTransactionCategory(Long transactionCategoryId);
}
