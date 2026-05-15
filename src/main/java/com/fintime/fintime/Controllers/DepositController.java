package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.DepositDto;
import com.fintime.fintime.Services.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/deposit")
public class DepositController {
    private final DepositService depositService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DepositDto> createDeposit(@RequestBody DepositDto depositDto){
    DepositDto deposit = depositService.createDeposit(depositDto);
    return ResponseEntity
            .created(URI.create("api/deposit" + deposit.getId()))
            .body(deposit);
    }

    @GetMapping
    public List<DepositDto> getAllDeposits(){
        return depositService.getAllDeposits();
    }

    @DeleteMapping("/{depositId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void deleteDeposit(@PathVariable Long depositId){
        depositService.deleteDeposit(depositId);
    }




 }
