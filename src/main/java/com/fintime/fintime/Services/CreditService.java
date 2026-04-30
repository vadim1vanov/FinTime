package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.CreditDto;

import java.math.BigDecimal;
import java.util.List;

public interface CreditService {
    List<CreditDto> getAllCredits();
//    CreditDto getCreditInfo(Long creditId);
    CreditDto createCredit(Long accountId, CreditDto creditDto);
//    void deleteCredit(Long creditId);
//    CreditDto editCredit(Long creditId, CreditDto creditDto);
    BigDecimal calculateMonthlyPayment(BigDecimal principalAmount, BigDecimal interestRate, Integer termMonths);
    BigDecimal calculateOverpayment(BigDecimal principalAmount,
                                    BigDecimal interestRate,
                                    Integer termMonths);
    BigDecimal calculateTotalAmount(BigDecimal principalAmount,
                                    BigDecimal interestRate,
                                    Integer termMonths);
    BigDecimal calculateRemainingBalance(BigDecimal principalAmount,
                                         BigDecimal interestRate,
                                         Integer termMonths, Integer numberPayment);
}
