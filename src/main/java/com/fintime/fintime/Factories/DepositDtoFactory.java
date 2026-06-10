package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.DepositDto;
import com.fintime.fintime.Models.DepositModel;
import org.springframework.stereotype.Component;

@Component
public class DepositDtoFactory {
    public static DepositDto makeDepositDto(DepositModel depositModel){
        return DepositDto.builder()
                .id(depositModel.getId())
                .status(depositModel.getStatus())
                .depositName(depositModel.getDepositName())
                .principalAmount(depositModel.getPrincipalAmount())
                .capitalizationFrequency(depositModel.getCapitalizationFrequency())
//                .currency(depositModel.getCurrency())
                .startDate(depositModel.getStartDate())
                .userId(depositModel.getUserId())
                .termDays(depositModel.getTermDays())
                .interestRate(depositModel.getInterestRate())
                .lastInterestAccrualDate(depositModel.getLastInterestAccrualDate())
                .createdAt(depositModel.getCreatedAt())
                .termType(depositModel.getTermType())
                .replenishmentFrequency(depositModel.getReplenishmentFrequency())
                .endDate(depositModel.getEndDate())
                .currentAmount(depositModel.getPrincipalAmount())
                .nextInterestAccrualDate(depositModel.getNextInterestAccrualDate())
                .build();
    }
}
