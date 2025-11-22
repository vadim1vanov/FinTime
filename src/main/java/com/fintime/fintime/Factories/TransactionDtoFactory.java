package com.fintime.fintime.Factories;

import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Models.TransactionModel;

public class TransactionDtoFactory {

    public TransactionDto makeTransactionDto(TransactionModel transactionModel){
        return TransactionDto.builder()
                .id(transactionModel.getId())
                .accountId(transactionModel.getAccountId())
                .amount(transactionModel.getAmount())
                .transactionalType(transactionModel.getTransactionalType())
                .description(transactionModel.getDescription())
                .createdAt(transactionModel.getCreatedAt())
                .build();
    }
}
