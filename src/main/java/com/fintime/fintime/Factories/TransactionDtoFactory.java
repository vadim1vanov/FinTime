package com.fintime.fintime.Factories;

import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Models.TransactionModel;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoFactory {

    public static TransactionDto makeTransactionDto(TransactionModel transactionModel){
        return TransactionDto.builder()
                .id(transactionModel.getId())
                .accountId(transactionModel.getAccountId())
                .accountTargetId(transactionModel.getAccountTargetId())
                .amount(transactionModel.getAmount())
                .transactionType(transactionModel.getTransactionType())
                .userId(transactionModel.getUserId())
                .description(transactionModel.getDescription())
                .createdAt(transactionModel.getCreatedAt())
                .financeGoalId(transactionModel.getFinanceGoalId())
                .build();
    }
}
