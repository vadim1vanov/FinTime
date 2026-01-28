package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.Models.AccountModel;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoFactory {

    public static AccountDto makeAccountDto(AccountModel accountModel){
        return AccountDto.builder()
                .id(accountModel.getId())
                .userId(accountModel.getUserId())
                .accountName(accountModel.getAccountName())
                .currency(accountModel.getCurrency())
                .balance(accountModel.getBalance())
                .status(accountModel.getStatus())
                .accountPosition(accountModel.getAccountPosition())
                .createdAt(accountModel.getCreatedAt())
                .build();
    }

}
