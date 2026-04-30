package com.fintime.fintime.Factories;

import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.Models.CreditModel;
import org.springframework.stereotype.Component;

@Component
public class CreditDtoFactory {
    public static CreditDto makeCreditDto(CreditModel creditModel){
        return CreditDto.builder()
                .id(creditModel.getId())
                .accountId(creditModel.getAccountId())
                .userId(creditModel.getUserId())
                .principalAmount(creditModel.getPrincipalAmount())
                .interestRate(creditModel.getInterestRate())
                .termMonths(creditModel.getTermMonths())
                .monthlyPayment(creditModel.getMonthlyPayment())
                .remainingBalance(creditModel.getRemainingBalance())
                .accruedInterest(creditModel.getAccruedInterest())
                .penaltyAmount(creditModel.getPenaltyAmount())
                .startDate(creditModel.getStartDate())
                .endDate(creditModel.getEndDate())
                .nextPaymentDate(creditModel.getNextPaymentDate())
                .type(creditModel.getType())
                .status(creditModel.getStatus())
                .purpose(creditModel.getPurpose())
                .createdAt(creditModel.getCreatedAt())
                .build();
    }
}
