package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.CurrenciesDto;
import com.fintime.fintime.Factories.CurrenciesDtoFactory;
import com.fintime.fintime.Models.CurrenciesModel;
import com.fintime.fintime.Repository.CurrenciesRepository;
import com.fintime.fintime.Services.CurrenciesService;
import com.fintime.fintime.Services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CurrenciesServiceImpl implements CurrenciesService {
    UserService userService;
    CurrenciesRepository currenciesRepository;

    @Override
    public List<CurrenciesDto> getAllCurrencies(){
        List<CurrenciesModel> currenciesModels = currenciesRepository.findAll();
        return currenciesModels.stream()
                .map(CurrenciesDtoFactory::makeCurrenciesDto)
                .toList();
    }
}
