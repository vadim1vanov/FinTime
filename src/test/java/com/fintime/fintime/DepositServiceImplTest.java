package com.fintime.fintime;

import com.fintime.fintime.Enums.CapitalizationFrequency;
import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Models.DepositModel;
import com.fintime.fintime.Repository.DepositRepository;
import com.fintime.fintime.Services.Impl.DepositServiceImpl;
import com.fintime.fintime.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DepositServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositServiceImpl depositService;

    private DepositModel deposit;

    @BeforeEach
    void setUp() {
        deposit = DepositModel.builder()
                .id(1L)
                .userId(1L)
                .depositName("Test deposit")
                .principalAmount(new BigDecimal("1000.00"))
                .currentAmount(new BigDecimal("1000.00"))
                .interestRate(new BigDecimal("12.00"))
                .termDays(365)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(LocalDate.now().plusMonths(11))
                .status(DepositStatus.ACTIVE)
                .capitalizationFrequency(CapitalizationFrequency.MONTHLY)
                .lastInterestAccrualDate(LocalDate.now().minusMonths(1))
                .nextInterestAccrualDate(LocalDate.now())
                .build();
    }

    @Test
    void accrueInterest_shouldIncreaseAmountAndShiftNextDate_whenAccrualDateReached() {
        depositService.accrueInterest(deposit);

        assertThat(deposit.getCurrentAmount()).isGreaterThan(new BigDecimal("1000.00"));
        assertThat(deposit.getNextInterestAccrualDate()).isEqualTo(LocalDate.now().plusMonths(1));
        verify(depositRepository).save(deposit);
    }

    @Test
    void accrueInterest_shouldDoNothing_whenNextAccrualDateNotReached() {
        deposit.setNextInterestAccrualDate(LocalDate.now().plusDays(10));
        BigDecimal before = deposit.getCurrentAmount();

        depositService.accrueInterest(deposit);

        assertThat(deposit.getCurrentAmount()).isEqualByComparingTo(before);
        assertThat(deposit.getNextInterestAccrualDate()).isEqualTo(LocalDate.now().plusDays(10));
        verify(depositRepository).save(deposit);
    }

    @Test
    void accrueInterest_shouldDoNothing_whenStatusIsNotActive() {
        deposit.setStatus(DepositStatus.CLOSED);
        BigDecimal before = deposit.getCurrentAmount();

        depositService.accrueInterest(deposit);

        assertThat(deposit.getCurrentAmount()).isEqualByComparingTo(before);
        assertThat(deposit.getNextInterestAccrualDate()).isEqualTo(LocalDate.now());
        verify(depositRepository).save(deposit);
    }

    @Test
    void calculateNextDate_shouldReturnCorrectMonthlyDate() {
        LocalDate baseDate = LocalDate.of(2026, 1, 15);

        LocalDate nextDate = invokeCalculateNextDate(baseDate, CapitalizationFrequency.MONTHLY);

        assertThat(nextDate).isEqualTo(LocalDate.of(2026, 2, 15));
    }

    @Test
    void getDepositById_shouldThrowAccessDenied_whenDepositBelongsToAnotherUser() {
        Long currentUserId = 2L;
        Long depositId = 1L;

        DepositModel anyDeposit = DepositModel.builder()
                .id(depositId)
                .userId(1L)
                .status(DepositStatus.ACTIVE)
                .build();

        org.mockito.Mockito.when(userService.getCurrentUserId()).thenReturn(currentUserId);
        org.mockito.Mockito.when(depositRepository.findById(depositId))
                .thenReturn(java.util.Optional.of(anyDeposit));

        org.junit.jupiter.api.Assertions.assertThrows(
                AccessDeniedException.class,
                () -> depositService.getDepositById(depositId)
        );
    }

    @Test
    void getDepositById_shouldThrowAccessDenied_whenDepositBelongsToAnotherUserWithoutRepositorySave() {
        deposit.setStatus(DepositStatus.ACTIVE);

        depositService.accrueInterest(deposit);

        verify(depositRepository).save(deposit);
        verify(depositRepository, never()).saveAndFlush(any());
    }

    private LocalDate invokeCalculateNextDate(LocalDate baseDate, CapitalizationFrequency frequency) {
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