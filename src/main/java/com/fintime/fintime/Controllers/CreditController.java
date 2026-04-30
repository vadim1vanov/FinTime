package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.CreditDto;
import com.fintime.fintime.Services.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/credit")
public class CreditController {
    private final CreditService creditService;


    @GetMapping
    public List<CreditDto> getAllCredits(){
        return creditService.getAllCredits();
    }

    @PostMapping("/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreditDto> createCredit(@PathVariable Long accountId, @RequestBody CreditDto creditDto){
        CreditDto credit = creditService.createCredit(accountId, creditDto);
        return ResponseEntity
                .created(URI.create("api/credit" + credit.getId()))
                .body(credit);
    }
}
