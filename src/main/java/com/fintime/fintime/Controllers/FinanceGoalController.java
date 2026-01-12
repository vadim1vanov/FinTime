package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.FinanceGoalDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Services.FinanceGoalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/financegoal")
@AllArgsConstructor
public class FinanceGoalController {
//    FinanceGoalService financeGoalService;
//
//    //СОЗДАНИЕ ФИН. ЦЕЛИ
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<FinanceGoalDto> createFinanceGoal(@RequestBody FinanceGoalDto financeGoalDto){
//         FinanceGoalDto financeGoal = financeGoalService.createFinanceGoal(financeGoalDto);
//         return ResponseEntity
//                 .created(URI.create("api/financegoal" + financeGoal.getId()))
//                 .body(financeGoal);
//    }
//
//    //СПИСОК ВСЕХ ФИН. ЦЕЛЕЙ
//    @GetMapping
//    public List<FinanceGoalDto> getAllFinanceGoals(){
//        return financeGoalService.getAllFinanceGoals();
//    }
//
//    //ИНФОРМАЦИЯ О ФИН. ЦЕЛИ
//    @GetMapping("/{financeGoalId}")
//    public FinanceGoalDto getFinanceGoalInfo(@PathVariable Long financeGoalId){
//        return financeGoalService.getFinanceGoalInfo(financeGoalId);
//    }
//
//    //РЕДАКТИРОВАНИЕ ФИН. ЦЕЛИ
//    @PatchMapping("/{financeGoalId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void editFinanceGoal(@PathVariable Long financeGoalId, @RequestBody FinanceGoalDto financeGoalDto){
//        financeGoalService.editFinanceGoal(financeGoalId, financeGoalDto);
//    }
//
//    //УСТАНОВЛЕНИЕ СТАТУСА ФИН. ЦЕЛИ - ВЫПОЛНЕНО
//    @PatchMapping("/{financeGoalId}/complete")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void completeFinanceGoal(@PathVariable Long financeGoalId){
//        financeGoalService.completeFinanceGoal(financeGoalId);
//    }
//
//    //УСТАНОВЛЕНИЕ СТАТУСА ФИН. ЦЕЛИ - НЕВЫПОЛНЕНО
//    @PatchMapping("/{financeGoalId}/unrealize")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void unrealizeFinanceGoal(@PathVariable Long financeGoalId){
//        financeGoalService.unrealizeFinanceGoal(financeGoalId);
//    }
//
//    //СПИСОК ВЫПОЛНЕННЫХ ФИН. ЦЕЛЕЙ
//    @GetMapping("/completed")
//    public List<FinanceGoalDto> getCompletedFinanceGoals(){
//        return financeGoalService.getCompletedFinanceGoals();
//    }
//
//    //СПИСОК НЕВЫПОЛНЕННЫХ ФИН. ЦЕЛЕЙ
//    @GetMapping("/unrealized")
//    public List<FinanceGoalDto> getUnrealizedFinanceGoals(){
//        return financeGoalService.getUnrealizedFinanceGoals();
//    }
//
//    //УДАЛИТЬ ФИН. ЦЕЛЬ
//    @DeleteMapping("/{financeGoalId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteFinanceGoal(@PathVariable Long financeGoalId){
//        financeGoalService.deleteFinanceGoal(financeGoalId);
//    }
//
//    //УДАЛИТЬ ВСЕ ФИН. ЦЕЛИ
//    @DeleteMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteAllFinanceGoals(){
//        financeGoalService.deleteAllFinanceGoals();
//    }
//
//
//    @PostMapping("/{financeGoalId}/income")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<TransactionDto> incomeFinanceGoal(@PathVariable Long financeGoalId,
//                                  @RequestBody TransactionDto transactionDto){
//        TransactionDto transaction = financeGoalService.incomeFinanceGoal(financeGoalId, transactionDto);
//        return ResponseEntity
//                .created(URI.create("api/transaction" + transaction.getId()))
//                .body(transaction);
//    }


}
