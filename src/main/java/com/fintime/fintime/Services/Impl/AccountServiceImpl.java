package com.fintime.fintime.Services.Impl;


import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.AccountDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Repository.AccountRepository;
import com.fintime.fintime.Services.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service

public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;


    AccountServiceImpl(AccountRepository accountRepository, AccountDtoFactory accountDtoFactory){
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(String accountName, String currency, BigDecimal balance){
        if(accountName.trim().isEmpty()){
            throw new BadRequestException("Введите название счёта!");
        }
        if(currency.trim().isEmpty()){
            throw new BadRequestException("Введите валюту счёта!");
        }
        if(balance == null){
            throw new BadRequestException("Введите баланс счёта!");
        }
        accountRepository
                .findByAccountName(accountName)
                .ifPresent(account ->{
                    throw new BadRequestException("Счёт с данным названием уже создан!");
                });
        AccountModel account = accountRepository.saveAndFlush(
                AccountModel.builder()
                        .accountName(accountName)
                        .currency(currency)
                        .balance(balance)
                        .status("active")
                        .createdAt(Instant.now())
                        .userId(1L)
                        .build()
        );
        return AccountDtoFactory.makeAccountDto(account);
    }

    @Override
    public AccountDto editAccount(Long accountId, AccountDto updatedAccountDto){
        AccountModel account = accountRepository.findById(accountId).orElseThrow(
                () -> new NotFoundException("Аккаунт с данным id не найден")
        );
        if(updatedAccountDto.getAccountName() != null){
            account.setAccountName(updatedAccountDto.getAccountName());
        }
        if(updatedAccountDto.getStatus() != null){
            account.setStatus(updatedAccountDto.getStatus());
        }
        if(updatedAccountDto.getBalance() != null){
            account.setBalance(updatedAccountDto.getBalance());
        }
        if(updatedAccountDto.getCurrency() != null){
            account.setCurrency(updatedAccountDto.getCurrency());
        }

        AccountModel updatedAccount = accountRepository.saveAndFlush(account);
        return AccountDtoFactory.makeAccountDto(updatedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts(){
        return  accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto).
                collect(Collectors.toList());
    }


    @Override
    public List<AccountDto> getActiveAccounts(){
        return accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto)
                .filter(accountDto -> Objects.equals(accountDto.getStatus(), "active"))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long accountId){
        AccountModel accountModel = accountRepository.findById(accountId).orElseThrow(
                () -> new BadRequestException("Счета с данным id не существует!")
        );
        accountRepository.delete(accountModel);
    }


    @Override
    public AccountDto closeAccount(Long accountId){
        AccountModel account = accountRepository.findById(accountId).orElseThrow(
                () -> new BadRequestException("Счёта с данным id не найдено!")
        );
        account.setStatus("closed");
        return AccountDtoFactory.makeAccountDto(account);
    }

    @Override
    public AccountDto restoreAccount(Long accountId){
        AccountModel account = accountRepository.findById(accountId).orElseThrow(
                () -> new BadRequestException("Счёта с данным id не найдено!")
        );
        account.setStatus("active");
        return AccountDtoFactory.makeAccountDto(account);
    }

    @Override
    public List<AccountDto> getClosedAccounts(){
        return accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto)
                .filter(accountDto -> Objects.equals(accountDto.getStatus(), "closed"))
                .collect(Collectors.toList());
    }
}
