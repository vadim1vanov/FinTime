package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.Enums.CreditStatus;
import com.fintime.fintime.Factories.CreditDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Models.CreditModel;
import com.fintime.fintime.Repository.CreditRepository;
import com.fintime.fintime.Services.AccountService;
import com.fintime.fintime.Services.CreditService;
import com.fintime.fintime.Services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CreditServiceImpl  implements CreditService {
    UserService userService;
    AccountService accountService;
    CreditRepository creditRepository;

    @Override
    public CreditDto createCredit(Long accountId, CreditDto creditDto){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        CreditModel creditModel = creditRepository.saveAndFlush(
                CreditModel.builder()
                        .accountId(accountId)
                        .userId(account.getUserId())
                        .principalAmount(creditDto.getPrincipalAmount())
                        .interestRate(creditDto.getInterestRate())
                        .termMonths(creditDto.getTermMonths())
                        .monthlyPayment(calculateMonthlyPayment(creditDto.getPrincipalAmount(),
                                creditDto.getInterestRate(), creditDto.getTermMonths()))
                        .remainingBalance(creditDto.getRemainingBalance())
                        .accruedInterest(creditDto.getAccruedInterest())
                        .penaltyAmount(creditDto.getPenaltyAmount())
                        .startDate(creditDto.getStartDate())
                        .endDate(creditDto.getEndDate())
                        .nextPaymentDate(creditDto.getNextPaymentDate())
                        .type(creditDto.getType())
                        .status(CreditStatus.ACTIVE)
                        .purpose(creditDto.getPurpose())
                        .createdAt(Instant.now())
                        .build()
        );
        return CreditDtoFactory.makeCreditDto(creditModel);
    }

    @Override
    public List<CreditDto> getAllCredits(){
        Long currentUserId = userService.getCurrentUserId();
        return creditRepository.findAll().stream()
                .map(CreditDtoFactory::makeCreditDto)
                .filter(creditDto -> Objects.equals(creditDto.getUserId(), currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal principalAmount,
                                              BigDecimal interestRate,
                                              Integer termMonths){
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        if(monthlyRate.compareTo(BigDecimal.ZERO) == 0){
            return principalAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }
        BigDecimal one = BigDecimal.ONE;
        BigDecimal calculateAnnuityFactor = monthlyRate.add(one).pow(termMonths);

        BigDecimal divideAnnuity = calculateAnnuityFactor.multiply(monthlyRate);
        BigDecimal divisorAnnuity =  calculateAnnuityFactor.subtract(BigDecimal.valueOf(1));

        return principalAmount.multiply(  divideAnnuity.divide(divisorAnnuity, 15, RoundingMode.HALF_UP)  )
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalAmount(BigDecimal principalAmount,
                                 BigDecimal interestRate,
                                 Integer termMonths){
        return calculateMonthlyPayment(principalAmount, interestRate, termMonths)
                .multiply(BigDecimal.valueOf(termMonths)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateOverpayment(BigDecimal principalAmount,
                                           BigDecimal interestRate,
                                           Integer termMonths ){
        return calculateTotalAmount(principalAmount, interestRate, termMonths).subtract(principalAmount);
    }

    @Override
    public BigDecimal calculateRemainingBalance(BigDecimal principalAmount,
                                                BigDecimal interestRate,
                                                Integer termMonths, Integer numberPayment){
        BigDecimal monthlyPayment = calculateMonthlyPayment(principalAmount, interestRate, termMonths);
        BigDecimal annuityFactor = interestRate.add(BigDecimal.valueOf(1)).pow(numberPayment);
        BigDecimal firstPart = annuityFactor.multiply(principalAmount);
        BigDecimal secondPart = monthlyPayment.multiply(
                (annuityFactor.subtract(BigDecimal.valueOf(1))).divide(interestRate, 10, RoundingMode.HALF_UP)
        );
        return firstPart.subtract(secondPart);
    }




}
