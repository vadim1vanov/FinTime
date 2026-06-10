package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.DepositDto;
import com.fintime.fintime.Enums.CapitalizationFrequency;
import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Enums.ReplenishmentFrequency;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.DepositDtoFactory;
import com.fintime.fintime.Models.DepositModel;
import com.fintime.fintime.Repository.DepositRepository;
import com.fintime.fintime.Services.DepositService;
import com.fintime.fintime.Services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final UserService userService;
    private final DepositRepository depositRepository;

    /**
     * Создаёт новый депозит для текущего пользователя.
     * Сохраняет его в БД в статусе ACTIVE и возвращает DTO.
     */
    @Override
    public DepositDto createDeposit(DepositDto depositDto) {
        Long currentUserId = userService.getCurrentUserId();

        DepositModel deposit = depositRepository.saveAndFlush(
                DepositModel.builder()
                        .userId(currentUserId)
                        .depositName(depositDto.getDepositName())
                        .principalAmount(depositDto.getPrincipalAmount())
                        .interestRate(depositDto.getInterestRate())
                        .termDays(depositDto.getTermDays())
                        .startDate(depositDto.getStartDate())
                        .endDate(depositDto.getEndDate())
                        .status(DepositStatus.ACTIVE)
//                        .currency(depositDto.getCurrency())
                        .createdAt(depositDto.getCreatedAt())
                        .replenishmentFrequency(depositDto.getReplenishmentFrequency())
                        .capitalizationFrequency(depositDto.getCapitalizationFrequency())
                        .termType(depositDto.getTermType())
                        .lastInterestAccrualDate(depositDto.getLastInterestAccrualDate())
                        .currentAmount(depositDto.getPrincipalAmount())
                        .build()
        );

        return DepositDtoFactory.makeDepositDto(deposit);
    }

    /**
     * Возвращает модель депозита по id с проверкой принадлежности текущему пользователю.
     * Если депозита нет или это не его депозит — бросает исключение.
     */
    @Override
    public DepositModel getDepositById(Long depositId) {
        Long currentUserId = userService.getCurrentUserId();
        DepositModel deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new NotFoundException("Deposit not found!"));

        if (!deposit.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have access!");
        }

        return deposit;
    }

    /**
     * Удаляет депозит по id.
     * Сначала проверяет доступ через getDepositById, затем удаляет запись из БД.
     */
    @Override
    public void deleteDeposit(Long depositId) {
        DepositModel deposit = getDepositById(depositId);
        depositRepository.delete(deposit);
    }

    /**
     * Возвращает список всех депозитов текущего пользователя (в DTO).
     * Из всех записей в БД фильтруются только те, у которых userId совпадает.
     */
    @Override
    public List<DepositDto> getAllDeposits() {
        Long currentUserId = userService.getCurrentUserId();
        return depositRepository.findAll().stream()
                .map(DepositDtoFactory::makeDepositDto)
                .filter(dto -> Objects.equals(dto.getUserId(), currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает простые проценты по депозиту (без капитализации).
     * Использует формулу P * R * T (основная сумма * ставка * время в годах).
     * Возвращает только сумму процентов.
     */
    @Override
    public BigDecimal calculateSimpleInterest(DepositDto depositDto) {
        LocalDate startDate = depositDto.getStartDate();
        LocalDate endDate = depositDto.getEndDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Deposit dates are missing");
        }
        if (depositDto.getInterestRate() == null || depositDto.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid interest rate");
        }
        if (depositDto.getPrincipalAmount() == null || depositDto.getPrincipalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid principal amount");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal timeInYears = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 15, RoundingMode.HALF_UP);

        // Simple Interest = P * R * T
        return depositDto.getPrincipalAmount()
                .multiply(depositDto.getInterestRate())
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                .multiply(timeInYears)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Рассчитывает итоговую сумму депозита с учётом капитализации процентов
     * по формуле сложного процента A = P * (1 + r/n)^(n*t).
     * Возвращает итоговую сумму (основная сумма + проценты).
     */
    @Override
    public BigDecimal calculateCompoundInterest(
            BigDecimal principal,
            BigDecimal annualRate,
            Integer termDays,
            CapitalizationFrequency capitalizationFrequency
    ) {
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid interest rate");
        }
        if (termDays == null || termDays <= 0) {
            throw new IllegalArgumentException("Term days must be positive");
        }

        BigDecimal rateDecimal = annualRate.divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        int n;

        switch (capitalizationFrequency) {
            case DAILY -> n = 365;
            case WEEKLY -> n = 52;
            case MONTHLY -> n = 12;
            case HALF_YEARLY -> n = 2;
            case YEARLY -> n = 1;
            case NONE -> n = 1;
            default -> throw new BadRequestException("Unsupported capitalization frequency: " + capitalizationFrequency);
        }

        BigDecimal timeInYears = BigDecimal.valueOf(termDays).divide(BigDecimal.valueOf(365), 15, RoundingMode.HALF_UP);
        BigDecimal nVal = BigDecimal.valueOf(n);
        BigDecimal rt = rateDecimal.divide(nVal, 15, RoundingMode.HALF_UP);

        BigDecimal base = BigDecimal.ONE.add(rt);
        BigDecimal power = timeInYears.multiply(BigDecimal.valueOf(n));

        BigDecimal amount = principal.multiply(base.pow(power.intValue()));
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Рассчитывает итоговую сумму депозита (сумма вклада + проценты).
     * Если капитализации нет — используется простой процент.
     * Если есть капитализация — используется сложный процент.
     */
    @Override
    public BigDecimal calculateTotalAmount(
            BigDecimal principal,
            BigDecimal annualRate,
            Integer termDays,
            CapitalizationFrequency capitalizationFrequency
    ) {
        if (capitalizationFrequency.equals(CapitalizationFrequency.NONE)) {
            return principal.add(
                    calculateSimpleInterest(
                            DepositDto.builder()
                                    .principalAmount(principal)
                                    .interestRate(annualRate)
                                    .startDate(LocalDate.now())
                                    .endDate(LocalDate.now().plusDays(termDays))
                                    .build()
                    )
            );
        }
        return calculateCompoundInterest(principal, annualRate, termDays, capitalizationFrequency);
    }

    /**
     * Рассчитывает чистую прибыль по депозиту (только проценты).
     * Вычитает начальную сумму из итоговой суммы вклада.
     */
    @Override
    public BigDecimal calculateProfit(
            BigDecimal principal,
            BigDecimal annualRate,
            Integer termDays,
            CapitalizationFrequency capitalizationFrequency
    ) {
        return calculateTotalAmount(principal, annualRate, termDays, capitalizationFrequency)
                .subtract(principal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Рассчитывает прогресс депозита (сколько процентов срока уже прошло).
     * Возвращает значение от 0 до 100% в зависимости от текущей даты относительно startDate/endDate.
     */
    @Override
    public BigDecimal calculateDepositProgress(Long depositId) {
        DepositModel deposit = getDepositById(depositId);
        LocalDate startDate = deposit.getStartDate();
        LocalDate endDate = deposit.getEndDate();
        LocalDate today = LocalDate.now();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Deposit dates are missing");
        }

        if (today.isBefore(startDate)) {
            return BigDecimal.ZERO;
        }
        if (today.isAfter(endDate)) {
            return BigDecimal.valueOf(100);
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }

        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);
        return BigDecimal.valueOf(elapsedDays)
                .divide(BigDecimal.valueOf(totalDays), 15, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Закрывает депозит: переводит статус в CLOSED и ставит текущую дату как endDate.
     * После изменения сохраняет обновлённую запись в БД.
     */
    @Override
    public void closeDeposit(Long depositId) {
        DepositModel deposit = getDepositById(depositId);
        deposit.setStatus(DepositStatus.CLOSED);
        deposit.setEndDate(LocalDate.now());
        depositRepository.saveAndFlush(deposit);
    }
    private long getPassedPeriods(
            LocalDate lastDate,
            LocalDate currentDate,
            CapitalizationFrequency frequency
    ) {

        return switch (frequency) {

            case DAILY ->
                    ChronoUnit.DAYS.between(
                            lastDate,
                            currentDate
                    );

            case WEEKLY ->
                    ChronoUnit.WEEKS.between(
                            lastDate,
                            currentDate
                    );

            case MONTHLY ->
                    ChronoUnit.MONTHS.between(
                            lastDate,
                            currentDate
                    );

            case HALF_YEARLY ->
                    ChronoUnit.MONTHS.between(
                            lastDate,
                            currentDate
                    ) / 6;

            case YEARLY ->
                    ChronoUnit.YEARS.between(
                            lastDate,
                            currentDate
                    );

            default -> 0;
        };
    }
    private BigDecimal calculatePeriodInterest(
            BigDecimal amount,
            BigDecimal annualRate,
            CapitalizationFrequency frequency
    ) {

        BigDecimal periodsPerYear;

        switch (frequency) {

            case DAILY ->
                    periodsPerYear =
                            BigDecimal.valueOf(365);

            case WEEKLY ->
                    periodsPerYear =
                            BigDecimal.valueOf(52);

            case MONTHLY ->
                    periodsPerYear =
                            BigDecimal.valueOf(12);

            case HALF_YEARLY ->
                    periodsPerYear =
                            BigDecimal.valueOf(2);

            case YEARLY ->
                    periodsPerYear =
                            BigDecimal.ONE;

            default ->
                    periodsPerYear =
                            BigDecimal.ONE;
        }

        return amount
                .multiply(
                        annualRate.divide(
                                BigDecimal.valueOf(100),
                                10,
                                RoundingMode.HALF_UP
                        )
                )
                .divide(
                        periodsPerYear,
                        10,
                        RoundingMode.HALF_UP
                );
    }
    @Transactional
    public void accrueInterest(DepositModel deposit) {
        LocalDate today = LocalDate.now();

        if (deposit.getNextInterestAccrualDate() == null) {
            deposit.setNextInterestAccrualDate(
                    calculateNextDate(deposit.getStartDate(), deposit.getCapitalizationFrequency())
            );
        }

        while (!today.isBefore(deposit.getNextInterestAccrualDate())
                && deposit.getStatus() == DepositStatus.ACTIVE) {

            BigDecimal interest = calculatePeriodInterest(
                    deposit.getCurrentAmount(),
                    deposit.getInterestRate(),
                    deposit.getCapitalizationFrequency()
            );

            deposit.setCurrentAmount(
                    deposit.getCurrentAmount().add(interest).setScale(2, RoundingMode.HALF_UP)
            );

            deposit.setNextInterestAccrualDate(
                    calculateNextDate(
                            deposit.getNextInterestAccrualDate(),
                            deposit.getCapitalizationFrequency()
                    )
            );
        }

        depositRepository.save(deposit);
    }

    private LocalDate calculateNextDate(LocalDate baseDate, CapitalizationFrequency frequency) {
        return switch (frequency) {
            case DAILY -> baseDate.plusDays(1);
            case WEEKLY -> baseDate.plusWeeks(1);
            case MONTHLY -> baseDate.plusMonths(1);
            case HALF_YEARLY -> baseDate.plusMonths(6);
            case YEARLY -> baseDate.plusYears(1);
            case NONE -> baseDate;
        };
    }

}