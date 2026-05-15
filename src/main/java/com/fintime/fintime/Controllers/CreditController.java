package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.CreditDataDto;

import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Services.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/credit")
public class CreditController {
    private final CreditService creditService;


    @GetMapping
    public List<CreditDataDto> getAllCredits(){
        return creditService.getAllCredits();
    }

    @PostMapping("/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreditDataDto> createCredit(@PathVariable Long accountId, @RequestBody CreditDataDto creditDto){
        CreditDataDto credit = creditService.createCredit(accountId, creditDto);
        return ResponseEntity
                .created(URI.create("api/credit" + credit.getId()))
                .body(credit);
    }

    @GetMapping("/{creditId}")
    public CreditDto getCreditInfo(@PathVariable Long creditId){
        return creditService.creditInfo(creditId);
    }

    @PatchMapping("/{creditId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeCredit(@PathVariable  Long creditId){
        creditService.closeCredit(creditId);
    }

    @PatchMapping("/{creditId}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void payCreditDebt(@PathVariable Long creditId, @RequestBody TransactionDto transactionDto){
        creditService.payCreditDebt(creditId, transactionDto);
    }

    @PatchMapping("/{creditId}/payoff")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void payOffCredit(@PathVariable Long creditId, @RequestBody TransactionDto transactionDto){
        creditService.payOffCredit(creditId, transactionDto);
    }

    @DeleteMapping("/{creditId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCredit(@PathVariable Long creditId){
        creditService.deleteCredit(creditId);
    }


}
