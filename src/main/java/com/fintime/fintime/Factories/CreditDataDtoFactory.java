package com.fintime.fintime.Factories;

import com.fintime.fintime.DTO.CreditDataDto;

import com.fintime.fintime.Models.CreditModel;
import org.springframework.stereotype.Component;

@Component
public class CreditDataDtoFactory {
    public static CreditDataDto makeCreditDataDto(CreditModel creditModel){
        return CreditDataDto.builder()
                .id(creditModel.getId())
                .accountId(creditModel.getAccountId())
                .userId(creditModel.getUserId())
                .principalAmount(creditModel.getPrincipalAmount())
                .interestRate(creditModel.getInterestRate())
                .termMonths(creditModel.getTermMonths())
                .startDate(creditModel.getStartDate())
                .endDate(creditModel.getEndDate())
                .type(creditModel.getType())
                .status(creditModel.getStatus())
                .purpose(creditModel.getPurpose())
                .remainingBalance(creditModel.getRemainingBalance())
                .createdAt(creditModel.getCreatedAt())
                .build();
    }
}
