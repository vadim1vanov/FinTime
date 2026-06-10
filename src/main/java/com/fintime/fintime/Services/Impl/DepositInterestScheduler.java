package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.Enums.DepositStatus;
import com.fintime.fintime.Repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositInterestScheduler {

    private final DepositRepository depositRepository;
    private final DepositServiceImpl depositService;

    @Scheduled(cron = "0 0 1 * * *")
    public void accrueInterestForAllDeposits() {

        depositRepository.findAll()
                .stream()
                .filter(d ->
                        d.getStatus() == DepositStatus.ACTIVE
                )
                .forEach(depositService::accrueInterest);
    }
}
