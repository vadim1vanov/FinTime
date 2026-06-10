package com.fintime.fintime.Controllers;

import com.fintime.fintime.DTO.BalanceHistoryDto;
import com.fintime.fintime.Services.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("api/analytic")
@RequiredArgsConstructor
public class AnalyticController {
    private final AnalyticService analyticService;

    @GetMapping("/total-credit-amount")
    public BigDecimal calculateTotalCreditAmount(){
        return analyticService.calculateTotalCreditAmount();
    }
    @GetMapping("/total-deposit-amount")
    public BigDecimal calculateTotalDepositAmount(){
        return analyticService.calculateTotalDepositAmount();
    }
    @GetMapping("/total-accounts-amount")
    public Map<String, BigDecimal> calculateTotalAccountsAmount(){
        return analyticService.calculateTotalAccountsAmount();
    }

    @GetMapping("/total-credit-overpayment")
    public BigDecimal calculateTotalCreditsOverpayment(){
       return analyticService.calculateTotalCreditsOverpayment();
    }
    @GetMapping("/{accountId}/balance-history")
    public List<BalanceHistoryDto> getBalanceHistory(
            @PathVariable Long accountId
    ){
        return analyticService.getBalanceHistory(accountId);
    }
}