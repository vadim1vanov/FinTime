package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.AccountDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Repository.AccountRepository;
import com.fintime.fintime.Repository.UserRepository;
import com.fintime.fintime.Services.AccountService;
import com.fintime.fintime.Services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;


    @Override
    @Transactional
    public AccountDto createAccount(AccountDto accountDto){
        Long currentUserId = userService.getCurrentUserId();

        if(accountRepository.findByAccountName(accountDto.getAccountName()).isPresent()) {
            throw new BadRequestException("Счёт с данным названием уже создан!");
        }
        List<AccountModel> accountModels = accountRepository.findAll().stream()
                        .filter(accountModel -> accountModel.getUserId().equals(currentUserId))
                        .toList();

        accountRepository.shiftPositionsDown(currentUserId);
        AccountModel account = accountRepository.saveAndFlush(
                AccountModel.builder()
                        .accountName(accountDto.getAccountName())
                        .currency(accountDto.getCurrency())
                        .balance(accountDto.getBalance())
                        .status("active")
                        .createdAt(Instant.now())
                        .userId(currentUserId)
                        .accountPosition(accountModels.size() + 1)
                        .build()
        );
        return AccountDtoFactory.makeAccountDto(account);
    }


    @Override
    @Transactional
    public void editAccount(Long accountId, AccountDto updatedAccountDto){
        AccountModel account = getAccountForCurrentUser(accountId);
        if(updatedAccountDto.getAccountName() != null && !updatedAccountDto.getAccountName().isBlank()){
            account.setAccountName(updatedAccountDto.getAccountName());
        }
        if(updatedAccountDto.getStatus() != null && !updatedAccountDto.getStatus().isBlank()) {
            account.setStatus(updatedAccountDto.getStatus());
        }
        if(updatedAccountDto.getBalance() != null) {
            account.setBalance(updatedAccountDto.getBalance());
        }
        if(updatedAccountDto.getCurrency() != null && !updatedAccountDto.getCurrency().isBlank()) {
            account.setCurrency(updatedAccountDto.getCurrency());
        }
        AccountModel updatedAccount = accountRepository.saveAndFlush(account);
        AccountDtoFactory.makeAccountDto(updatedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts(){
        Long currentUserId = userService.getCurrentUserId();
        return  accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto)
                .filter(accountDto -> Objects.equals(accountDto.getUserId(), currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getAccount(Long accountId){
        AccountModel account = getAccountForCurrentUser(accountId);
        return AccountDtoFactory.makeAccountDto(account);
    }

    @Override
    public List<AccountDto> getActiveAccounts(){
        Long currentUserId = userService.getCurrentUserId();
        return accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto)
                .filter(accountDto -> Objects.equals(accountDto.getStatus(), "active") &&
                        Objects.equals(accountDto.getUserId(), currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> getClosedAccounts(){
        return accountRepository.findAll().stream()
                .map(AccountDtoFactory::makeAccountDto)
                .filter(accountDto -> Objects.equals(accountDto.getStatus(), "closed"))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long accountId){
        AccountModel account = getAccountForCurrentUser(accountId);
        accountRepository.delete(account);
    }

    @Override
    public void closeAccount(Long accountId){
        AccountModel account = getAccountForCurrentUser(accountId);
        account.setStatus("closed");
        AccountModel updatedAccount = accountRepository.saveAndFlush(account);
        AccountDtoFactory.makeAccountDto(updatedAccount);
    }

    @Override
    public void restoreAccount(Long accountId){
        AccountModel account = getAccountForCurrentUser(accountId);
        account.setStatus("active");
        AccountModel updatedAccount = accountRepository.saveAndFlush(account);
        AccountDtoFactory.makeAccountDto(updatedAccount);
    }

    @Override
    public void deleteAllAccounts(){
        Long currentUserId = userService.getCurrentUserId();
        List<AccountModel> accounts = accountRepository.findAll().stream()
                .filter(account -> account.getUserId().equals(currentUserId))
                .toList();
        accountRepository.deleteAll(accounts);
    }

    @Override
    public AccountModel getAccountForCurrentUser(Long accountId){
        Long currentUser = userService.getCurrentUserId();
        AccountModel account = accountRepository.findById(accountId).orElseThrow(
                () -> new NotFoundException("Account not found!"));
        if(!account.getUserId().equals(currentUser)){
            throw new AccessDeniedException("You don't have access!");
        }
        return account;
    }

    @Override
    public void increaseAccountBalance(Long accountId, BigDecimal amount){
        AccountModel accountModel = getAccountForCurrentUser(accountId);
        accountModel.setBalance(accountModel.getBalance().add(amount));
        accountRepository.saveAndFlush(accountModel);
    }

    @Override
    public void decreaseAccountBalance(Long accountId, BigDecimal amount){
        AccountModel accountModel = getAccountForCurrentUser(accountId);
        accountModel.setBalance(accountModel.getBalance().subtract(amount));
        accountRepository.saveAndFlush(accountModel);
    }

    @Override
    @Transactional
    public void reorderAccounts(List<AccountDto> newOrder) {
        Long currentUserId = userService.getCurrentUserId();
        List<AccountModel> userAccounts = accountRepository.findAll().stream()
                .filter(accountModel -> accountModel.getUserId().equals(currentUserId))
                .toList();

        Map<Long, AccountModel> accountMap = userAccounts.stream()
                .collect(Collectors.toMap(AccountModel::getId, a -> a));

        for (int i = 0; i < newOrder.size(); i++) {
            AccountDto dto = newOrder.get(i);
            AccountModel account = accountMap.get(dto.getId());
            if (account != null) {
                account.setAccountPosition(i);
            }
        }

        accountRepository.saveAll(userAccounts);
    }



}
