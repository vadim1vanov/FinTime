package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("api/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    //СОЗДАНИЕ ТРАНЗАКЦИИ "ПОПОЛНИТЬ СЧЁТ"
    @PostMapping("/{accountId}/income")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionDto> createIncomeTransaction(@PathVariable Long accountId,
                                          @RequestBody TransactionDto transactionDto){
        TransactionDto transaction = transactionService.createIncomeTransaction(accountId, transactionDto);
        return ResponseEntity
                .created(URI.create("api/transaction" + transaction.getId()))
                .body(transaction);
    }

    //СПИСОК ВСЕХ ТРАНЗАКЦИЙ ПО СЧЁТУ
    @GetMapping("/{accountId}")
    public List<TransactionDto> getAllTransactions(@PathVariable Long accountId){
        return transactionService.getAllTransactionsByAccount(accountId);
    }

    //ИНФОРМАЦИЯ О ТРАНЗАКЦИИ
    @GetMapping("/{accountId}/{transactionId}")
    public TransactionDto getTransactionInfo(@PathVariable Long accountId, @PathVariable Long transactionId){
        return transactionService.getTransactionInfo(accountId, transactionId);
    }

    //УДАЛЕНИЕ ТРАНЗАКЦИИ
    @DeleteMapping("/{accountId}/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long accountId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(accountId, transactionId);
    }

    //УДАЛЕНИЕ ВСЕХ ТРАНЗАКЦИЙ ПО СЧЁТУ
    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllTransactions(@PathVariable Long accountId){
        transactionService.deleteAllTransactionsByAccount(accountId);
    }

    //СОЗДАНИЕ ТРАНЗАКЦИИ "СНЯТЬ СО СЧЁТА"
    @PostMapping("/{accountId}/expense")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionDto> createExpenseTransaction(@PathVariable Long accountId,
                                                   @RequestBody TransactionDto transactionDto){
        TransactionDto transaction = transactionService.createExpenseTransaction(accountId, transactionDto);
        return ResponseEntity
                .created(URI.create("api/transaction" + transaction.getId()))
                .body(transaction);
    }

    //СОЗДАНИЕ ТРАНЗАКЦИИ "ПЕРЕВОД НА ДРУГОЙ СЧЁТ"
    @PostMapping("/{accountId}/{targetAccountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionDto> createTransferTransaction(@PathVariable Long accountId,
                                            @PathVariable Long targetAccountId,
                                            @RequestBody TransactionDto transactionDto){
        TransactionDto transaction = transactionService
                .createTransferTransaction(accountId, targetAccountId, transactionDto);
        return ResponseEntity
                .created(URI.create("api/transaction" + transaction.getId()))
                .body(transaction);
    }

    //СПИСОК ВСЕХ ТРАНЗАКЦИЙ ПО ВСЕМ СЧЕТАМ
    @GetMapping
    public List<TransactionDto> getAllTransactions(){
        return transactionService.getAllTransactions();
    }

    //УДАЛЕНИЕ ВСЕХ ТРАНЗАКЦИЙ ПО ВСЕМ СЧЕТАМ
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllTransactions(){
        transactionService.deleteAllTransactions();
    }


}
