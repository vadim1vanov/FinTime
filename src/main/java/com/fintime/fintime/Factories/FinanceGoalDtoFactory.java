package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.FinanceGoalDto;
import com.fintime.fintime.Models.FinanceGoalModel;
import org.springframework.stereotype.Component;

@Component
public class FinanceGoalDtoFactory {

    public static FinanceGoalDto makeFinanceGoalDto(FinanceGoalModel financeGoalModel){
        return FinanceGoalDto.builder()
                .id(financeGoalModel.getId())
                .userId(financeGoalModel.getUserId())
                .name(financeGoalModel.getName())
                .targetAmount(financeGoalModel.getTargetAmount())
                .currentAmount(financeGoalModel.getCurrentAmount())
                .deadline(financeGoalModel.getDeadline())
                .percentGoal(financeGoalModel.getPercentGoal())
                .status(financeGoalModel.getStatus())
                .createdAt(financeGoalModel.getCreatedAt())
                .build();
    }
}
