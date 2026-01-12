package com.fintime.fintime.Controllers;

import com.fintime.fintime.DTO.CurrenciesDto;
import com.fintime.fintime.Services.CurrenciesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/currencies")
public class CurrenciesController {
    CurrenciesService currenciesService;

    @GetMapping
    public List<CurrenciesDto> getAllCurrencies(){
        return currenciesService.getAllCurrencies();
    }
}
