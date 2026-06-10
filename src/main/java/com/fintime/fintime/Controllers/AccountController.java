package com.fintime.fintime.Controllers;

import com.fintime.fintime.DTO.*;
import com.fintime.fintime.Services.AnalyticService;
import com.fintime.fintime.Services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AnalyticService analyticService;

    //СОЗДАНИЕ СЧЁТА
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto){
        AccountDto account = accountService.createAccount(accountDto);
        return ResponseEntity
                .created(URI.create("api/accounts" + account.getId()))
                .body(account);
    }

    //РЕДАКТИРОВАНИЕ СЧЁТА
    @PatchMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editAccount(@PathVariable Long accountId, @RequestBody AccountDto accountDto){
        accountService.editAccount(accountId, accountDto);
    }

    //ПОЛУЧЕНИЕ ВСЕХ СЧЕТОВ
    @GetMapping
    public List<AccountDto> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    //ПОЛУЧЕНИЕ СЧЁТА
    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable Long accountId){
        return accountService.getAccount(accountId);
    }

    //ПОЛУЧЕНИЕ АКТИВНЫХ СЧЕТОВ
    @GetMapping("/active")
    public List<AccountDto> getActiveAccounts(){
        return accountService.getActiveAccounts();
    }

    //ПОЛУЧЕНИЕ ЗАКРЫТЫХ СЧЕТОВ
    @GetMapping("closed")
    public List<AccountDto> getClosedAccounts(){
        return accountService.getClosedAccounts();
    }

    //ЗАКРЫТЬ СЧЁТ
    @PatchMapping("/{accountId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable Long accountId){
        accountService.closeAccount(accountId);
    }

    //ВОССТАНОВИТЬ СЧЁТ
    @PatchMapping("/{accountId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreAccount(@PathVariable Long accountId){
        accountService.restoreAccount(accountId);
    }

    //УДАЛИТЬ СЧЁТ
    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long accountId){
        accountService.deleteAccount(accountId);
    }

    //УДАЛИТЬ ВСЕ СЧЕТА
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllAccounts(){
        accountService.deleteAllAccounts();
    }

    @PostMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderAccounts(@RequestBody List<AccountDto> newOrder) {
        accountService.reorderAccounts(newOrder);
    }

    @GetMapping("/{accountId}/info")
    public AccountInfoDto getAccountInfo(@PathVariable Long accountId){
        return analyticService.getAccountAnalytics(accountId);
    }




}
