package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.DepositDto;
import com.fintime.fintime.Enums.CapitalizationFrequency;
import com.fintime.fintime.Models.DepositModel;
import java.math.BigDecimal;
import java.util.List;

public interface DepositService {

    /**
     * Создаёт новый депозит и возвращает DTO с заполненными полями.
     */
    DepositDto createDeposit(DepositDto depositDto);

    /**
     * Удаляет депозит по id (с проверкой владельца внутри реализации).
     */
    void deleteDeposit(Long depositId);

    /**
     * Возвращает модель депозита по id (с проверкой доступа текущего пользователя).
     * Нужен для внутренних операций (например, расчётов, закрытия и т.п.).
     */
    DepositModel getDepositById(Long depositId);

    /**
     * Возвращает список всех депозитов текущего пользователя (в виде DTO).
     */
    List<DepositDto> getAllDeposits();

    /**
     * Рассчитывает простой процент по депозиту (без капитализации).
     * Возвращает только сумму процентов.
     */
    BigDecimal calculateSimpleInterest(DepositDto depositDto);


     BigDecimal calculateCompoundInterest(
         BigDecimal principal, BigDecimal annualRate, Integer termDays,
         CapitalizationFrequency capitalizationFrequency);

     BigDecimal calculateTotalAmount(
         BigDecimal principal, BigDecimal annualRate, Integer termDays,
         CapitalizationFrequency capitalizationFrequency);

     BigDecimal calculateProfit(
         BigDecimal principal, BigDecimal annualRate, Integer termDays,
         CapitalizationFrequency capitalizationFrequency);

     BigDecimal calculateDepositProgress(Long depositId);

     void closeDeposit(Long depositId);

}