package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.CreditDataDto;

import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Enums.CreditStatus;
import com.fintime.fintime.Enums.CreditType;
import com.fintime.fintime.Enums.TransactionType;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.CreditDataDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Models.CreditModel;
import com.fintime.fintime.Repository.AccountRepository;
import com.fintime.fintime.Repository.CreditRepository;
import com.fintime.fintime.Services.AccountService;
import com.fintime.fintime.Services.CreditService;
import com.fintime.fintime.Services.TransactionService;
import com.fintime.fintime.Services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl  implements CreditService {
    private final UserService userService;
    private final AccountService accountService;
    private final CreditRepository creditRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

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
                                                Integer termMonths,
                                                Integer numberPayment) {
        BigDecimal monthlyRate = interestRate
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal paid = calculateMonthlyPayment(principalAmount, interestRate, termMonths)
                    .multiply(BigDecimal.valueOf(numberPayment));
            return principalAmount.subtract(paid).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(principalAmount, interestRate, termMonths);
        BigDecimal onePlusRatePowN = BigDecimal.ONE.add(monthlyRate).pow(numberPayment);

        BigDecimal firstPart = principalAmount.multiply(onePlusRatePowN);
        BigDecimal secondPart = monthlyPayment.multiply(
                onePlusRatePowN.subtract(BigDecimal.ONE).divide(monthlyRate, 15, RoundingMode.HALF_UP)
        );

        return firstPart.subtract(secondPart).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public CreditDataDto createCredit(Long accountId, CreditDataDto creditDto){
        AccountModel account = accountService.getAccountForCurrentUser(accountId);
        BigDecimal remainingBalance = calculateRemainingBalance(
                creditDto.getPrincipalAmount(),
                creditDto.getInterestRate(),
                creditDto.getTermMonths(),
                0);
        CreditModel creditModel = creditRepository.saveAndFlush(
                CreditModel.builder()
                        .accountId(accountId)
                        .userId(account.getUserId())
                        .principalAmount(creditDto.getPrincipalAmount())
                        .interestRate(creditDto.getInterestRate())
                        .termMonths(creditDto.getTermMonths())
                        .startDate(creditDto.getStartDate())
                        .endDate(LocalDate.now())
                        .type(CreditType.CREDIT_LINE)
                        .status(CreditStatus.ACTIVE)
                        .purpose(creditDto.getPurpose())
                        .createdAt(Instant.now())
                        .remainingBalance(calculateTotalAmount(
                                creditDto.getPrincipalAmount(),
                                creditDto.getInterestRate(),
                                creditDto.getTermMonths()))
                        .lastPaymentDate(null)
                        .paidInterest(BigDecimal.valueOf(228))
                        .build()
        );
        return CreditDataDtoFactory.makeCreditDataDto(creditModel);
    }

    @Override
    public List<CreditDataDto> getAllCredits(){
        Long currentUserId = userService.getCurrentUserId();
        return creditRepository.findAll().stream()
                .map(credit ->{
                    CreditDataDto creditDataDto =
                            CreditDataDtoFactory.makeCreditDataDto(credit);
                    creditDataDto.setProgress(calculateCreditProgress(credit.getId()));
                    return creditDataDto;
                })
                .filter(creditDto -> Objects.equals(creditDto.getUserId(), currentUserId))
                .collect(Collectors.toList());
    }





    @Override
    public CreditModel getCreditForCurrentUser(Long creditId){
        Long currentUser = userService.getCurrentUserId();
        CreditModel credit  = creditRepository.findById(creditId).orElseThrow(
                () -> new NotFoundException("Account not found!"));
        if(!credit.getUserId().equals(currentUser)){
            throw new AccessDeniedException("You don't have access!");
        }
        return credit;
    }


    @Override
    public CreditDto creditInfo(Long creditId){
        CreditModel credit = getCreditForCurrentUser(creditId);
        BigDecimal progress = calculateCreditProgress(creditId);
        return CreditDto.builder()
                .id(creditId)
                .nextPaymentDate(LocalDate.now())
                .type(CreditType.CREDIT_LINE)
                .paidInterest(BigDecimal.valueOf(228))
                .startDate(credit.getStartDate())
                .endDate(credit.getEndDate())
                .userId(credit.getUserId())
                .accountId(credit.getAccountId())
                .createdAt(credit.getCreatedAt())
                .purpose(credit.getPurpose())
                .status(credit.getStatus())
                .termMonths(credit.getTermMonths())
                .principalAmount(credit.getPrincipalAmount())
                .interestRate(credit.getInterestRate())
                .monthlyPayment(calculateMonthlyPayment(
                        credit.getPrincipalAmount(),
                        credit.getInterestRate(),
                        credit.getTermMonths()))
                .remainingBalance(calculateRemainingBalance(
                        credit.getPrincipalAmount(),
                        credit.getInterestRate(),
                        credit.getTermMonths(),
                        1))
                .progress(progress)
                .build();
    }

    @Override
    public void closeCredit(Long creditId){
        CreditModel credit = getCreditForCurrentUser(creditId);
        credit.setStatus(CreditStatus.PAID_OFF);
        CreditModel updatedCredit = creditRepository.saveAndFlush(credit);
    }

    @Override
    public void payCreditDebt(Long creditId, TransactionDto transactionDto) {
        CreditModel credit = getCreditForCurrentUser(creditId);
        AccountModel account = accountService.getAccountForCurrentUser(credit.getAccountId());

        if (transactionDto.getAmount() == null
                || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (transactionDto.getAmount().compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed account balance");
        }
        if (transactionDto.getAmount().compareTo(credit.getRemainingBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed remaining balance");
        }


        transactionDto.setAccountId(credit.getAccountId());
        transactionDto.setUserId(credit.getUserId());
        transactionDto.setCreatedAt(Instant.now());
        transactionDto.setTransactionType(TransactionType.EXPENSE);


        TransactionDto savedTransaction = transactionService.createExpenseTransaction(
                credit.getAccountId(),
                transactionDto
        );


        credit.setRemainingBalance(
                credit.getRemainingBalance().subtract(transactionDto.getAmount())
        );

        creditRepository.saveAndFlush(credit);
    }

    @Override
    public void payOffCredit(Long creditId, TransactionDto transactionDto) {
        CreditModel credit = getCreditForCurrentUser(creditId);
        AccountModel account = accountService.getAccountForCurrentUser(credit.getAccountId());

        BigDecimal amount = credit.getRemainingBalance();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Credit already paid off");
        }

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Account balance is insufficient to pay off the credit");
        }


        transactionDto.setAmount(amount);
        transactionDto.setAccountId(credit.getAccountId());
        transactionDto.setUserId(credit.getUserId());
        transactionDto.setCreatedAt(Instant.now());
        transactionDto.setTransactionType(TransactionType.EXPENSE);


        payCreditDebt(creditId, transactionDto);


        credit.setStatus(CreditStatus.PAID_OFF);
        creditRepository.saveAndFlush(credit);
    }

    @Override
    public BigDecimal calculateCreditProgress(Long creditId){
        CreditModel credit = getCreditForCurrentUser(creditId);
        return (calculateTotalAmount(credit.getPrincipalAmount(), credit.getInterestRate(), credit.getTermMonths())
                .subtract(credit.getRemainingBalance()))
                .divide(calculateTotalAmount(credit.getPrincipalAmount(), credit.getInterestRate(), credit.getTermMonths()), 15, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void deleteCredit(Long creditId){
        CreditModel credit = getCreditForCurrentUser(creditId);
        creditRepository.delete(credit);
    }






}
