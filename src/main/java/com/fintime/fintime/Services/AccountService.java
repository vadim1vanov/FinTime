package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.DTO.AccountInfoDto;
import com.fintime.fintime.Models.AccountModel;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    void editAccount(Long accountId, AccountDto updatedAccountDto);
    List<AccountDto> getAllAccounts();
    List<AccountDto> getActiveAccounts();
    void deleteAccount(Long accountId);
    void deleteAllAccounts();
    void closeAccount(Long accountId);
    void restoreAccount(Long accountId);
    List<AccountDto> getClosedAccounts();
    AccountDto getAccount(Long accountId);
    AccountModel getAccountForCurrentUser(Long accountId);
    void increaseAccountBalance(Long accountId, BigDecimal amount);
    void decreaseAccountBalance(Long accountId, BigDecimal amount);
    void reorderAccounts(List<AccountDto> newOrder);

}
