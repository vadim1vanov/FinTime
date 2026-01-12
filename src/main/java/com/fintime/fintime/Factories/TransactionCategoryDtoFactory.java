package com.fintime.fintime.Factories;

import com.fintime.fintime.DTO.TransactionCategoryDto;
import com.fintime.fintime.Models.TransactionCategoryModel;
import org.springframework.stereotype.Component;

@Component
public class TransactionCategoryDtoFactory {
    public static TransactionCategoryDto makeTransactionCategoryDto(TransactionCategoryModel transactionCategoryModel){
        return TransactionCategoryDto.builder()
                .id(transactionCategoryModel.getId())
                .transactionType(transactionCategoryModel.getTransactionType())
                .categoryName(transactionCategoryModel.getName())
                .userId(transactionCategoryModel.getUserId())
                .build();
    }
}
