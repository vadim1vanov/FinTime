package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.CurrenciesDto;
import com.fintime.fintime.Models.CurrenciesModel;
import org.springframework.stereotype.Component;

@Component
public class CurrenciesDtoFactory {
    public static CurrenciesDto makeCurrenciesDto(CurrenciesModel currenciesModel){
        return CurrenciesDto.builder()
                .id(currenciesModel.getId())
                .code(currenciesModel.getCode())
                .name(currenciesModel.getName())
                .symbol(currenciesModel.getSymbol())
                .build();
    }

}
