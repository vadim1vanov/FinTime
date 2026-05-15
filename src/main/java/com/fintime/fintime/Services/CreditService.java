package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.CreditDataDto;

import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Models.CreditModel;

import java.math.BigDecimal;
import java.util.List;

public interface CreditService {
    List<CreditDataDto> getAllCredits();
//    CreditDto getCreditInfo(Long creditId);
    CreditDataDto createCredit(Long accountId, CreditDataDto creditDto);
    void deleteCredit(Long creditId);
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
//    BigDecimal calculatePercentPerMonth(BigDecimal principalAmount,
//                                        BigDecimal remainingBalance, BigDecimal interestRate);
//
    CreditDto creditInfo(Long creditId);
    CreditModel getCreditForCurrentUser(Long creditId);
    void closeCredit(Long creditId);
    void payCreditDebt(Long creditId, TransactionDto transactionDto);
    void payOffCredit(Long creditId, TransactionDto transactionDto);
    BigDecimal calculateCreditProgress(Long creditId);
}
