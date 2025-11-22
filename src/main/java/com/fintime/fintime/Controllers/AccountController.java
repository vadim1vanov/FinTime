package com.fintime.fintime.Controllers;

import com.fintime.fintime.DTO.*;
import com.fintime.fintime.Repository.*;
import com.fintime.fintime.Services.Impl.AccountServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Transactional
@RestController
public class AccountController {
    final AccountServiceImpl accountService;
    final AccountRepository accountRepository;

    AccountController(AccountServiceImpl accountService, AccountRepository accountRepository){
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public static final String CREATE_ACCOUNT = "/accounts/create";
    public static final String EDIT_ACCOUNT = "/accounts/edit/{account_id}";
    public static final String DELETE_ACCOUNT = "/accounts/delete/{account_id}";
    public static final String CLOSE_ACCOUNT = "/accounts/close/{account_id}";
    public static final String RESTORE_ACCOUNT = "/accounts/restore/{account_id}";
    public static final String GET_ALL_ACCOUNTS = "/accounts/all";
    public static final String GET_ACTIVE_ACCOUNTS = "/accounts/active";
    public static final String GET_CLOSED_ACCOUNTS = "/accounts/closed";

    @PostMapping(CREATE_ACCOUNT)
    public AccountDto createAccount(@RequestParam String accountName, @RequestParam String currency,
                                    @RequestParam BigDecimal balance){
        return accountService.createAccount(accountName, currency, balance);
    }

    @PatchMapping(EDIT_ACCOUNT)
    public AccountDto editAccount(@PathVariable("account_id") Long accountId,
                                  @RequestBody AccountDto updatedAccountDto){
        return accountService.editAccount(accountId, updatedAccountDto);
    }

    @GetMapping(GET_ALL_ACCOUNTS)
    public List<AccountDto> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping(GET_ACTIVE_ACCOUNTS)
    public List<AccountDto> getActiveAccounts(){
        return accountService.getActiveAccounts();
    }

    @GetMapping(GET_CLOSED_ACCOUNTS)
    public List<AccountDto> getClosedAccounts(){
        return accountService.getClosedAccounts();
    }

    @DeleteMapping(DELETE_ACCOUNT)
    public void deleteAccount(@PathVariable("account_id") Long accountId){
        accountService.deleteAccount(accountId);
    }

    @PatchMapping(CLOSE_ACCOUNT)
    public AccountDto closeAccount(@PathVariable("account_id") Long accountId){
        return accountService.closeAccount(accountId);
    }


    @PostMapping(RESTORE_ACCOUNT)
    public AccountDto restoreAccount(@PathVariable("account_id") Long accountId){
        return accountService.restoreAccount(accountId);
    }



}
