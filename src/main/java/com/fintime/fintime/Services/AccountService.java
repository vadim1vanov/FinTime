package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.AccountDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDto createAccount(String accountName, String currency, BigDecimal balance);
    AccountDto editAccount(Long accountId, AccountDto updatedAccountDto);
    List<AccountDto> getAllAccounts();
    List<AccountDto> getActiveAccounts();
    void deleteAccount(Long accountId);
    AccountDto closeAccount(Long accountId);
    AccountDto restoreAccount(Long accountId);
    List<AccountDto> getClosedAccounts();
}
