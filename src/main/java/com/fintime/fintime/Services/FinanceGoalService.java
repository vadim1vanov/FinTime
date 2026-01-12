package com.fintime.fintime.Services;


import com.fintime.fintime.DTO.FinanceGoalDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Models.FinanceGoalModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface FinanceGoalService {
    FinanceGoalDto createFinanceGoal(FinanceGoalDto financeGoalDto);
    FinanceGoalDto getFinanceGoalInfo(Long financeGoalId);
    List<FinanceGoalDto> getAllFinanceGoals();
    void editFinanceGoal(Long financeGoalId, FinanceGoalDto updatedFinanceGoalDto);
    void completeFinanceGoal(Long financeGoalId);
    void unrealizeFinanceGoal(Long financeGoalId);
    List<FinanceGoalDto> getCompletedFinanceGoals();
    List<FinanceGoalDto> getUnrealizedFinanceGoals();
    void deleteFinanceGoal(Long financeGoalId);
    void deleteAllFinanceGoals();
    //TransactionDto incomeFinanceGoal(Long financeGoalId, TransactionDto transactionDto);
    FinanceGoalModel getFinanceGoalForCurrentUser(Long financeGoalId);
    void increaseFinanceGoal(Long financeGoalId, BigDecimal amount);
    void decreaseFinanceGoal(Long financeGoalId, BigDecimal amount);
    Double percentCompleted(Long financeGoalId);
}
