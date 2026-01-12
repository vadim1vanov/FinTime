package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.CurrenciesDto;

import java.util.List;

public interface CurrenciesService {
    List<CurrenciesDto> getAllCurrencies();
}
