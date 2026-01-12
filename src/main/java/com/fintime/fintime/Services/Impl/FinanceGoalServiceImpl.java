package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.AccountDto;
import com.fintime.fintime.DTO.FinanceGoalDto;
import com.fintime.fintime.DTO.TransactionDto;
import com.fintime.fintime.Enums.FinanceGoalStatus;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.AccountDtoFactory;
import com.fintime.fintime.Factories.FinanceGoalDtoFactory;
import com.fintime.fintime.Models.AccountModel;
import com.fintime.fintime.Models.FinanceGoalModel;
import com.fintime.fintime.Models.TransactionModel;
import com.fintime.fintime.Repository.AccountRepository;
import com.fintime.fintime.Repository.FinanceGoalRepository;
import com.fintime.fintime.Repository.TransactionRepository;
import com.fintime.fintime.Services.AccountService;
import com.fintime.fintime.Services.FinanceGoalService;
import com.fintime.fintime.Services.TransactionService;
import com.fintime.fintime.Services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FinanceGoalServiceImpl implements FinanceGoalService {
    FinanceGoalRepository financeGoalRepository;
    UserService userService;
    AccountService accountService;


    @Override
    public FinanceGoalDto createFinanceGoal(FinanceGoalDto financeGoalDto){
        Long currentUserId = userService.getCurrentUserId();
        FinanceGoalModel financeGoalModel = financeGoalRepository.saveAndFlush(
                FinanceGoalModel.builder()
                        .name(financeGoalDto.getName())
                        .userId(currentUserId)
                        .status(FinanceGoalStatus.PROGRESS)
                        .currentAmount(financeGoalDto.getCurrentAmount())
                        .targetAmount(financeGoalDto.getTargetAmount())
                        .deadline(financeGoalDto.getDeadline())
                        .createdAt(Instant.now())
                        .percentGoal(0.0)
                        .build()
        );
        return FinanceGoalDtoFactory.makeFinanceGoalDto(financeGoalModel);
    }

    @Override
    public FinanceGoalDto getFinanceGoalInfo(Long financeGoalId){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        return FinanceGoalDtoFactory.makeFinanceGoalDto(financeGoalModel);
    }

    @Override
    public List<FinanceGoalDto> getAllFinanceGoals(){
        Long currentUserId = userService.getCurrentUserId();
        return financeGoalRepository.findAll().stream()
                .map(FinanceGoalDtoFactory::makeFinanceGoalDto)
                .filter(financeGoalDto -> financeGoalDto.getUserId().equals(currentUserId))
                .toList();
    }

    @Override
    public void editFinanceGoal(Long financeGoalId, FinanceGoalDto updatedFinanceGoalDto){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        if(updatedFinanceGoalDto.getName() != null && !updatedFinanceGoalDto.getName().isBlank()){
            financeGoalModel.setName(updatedFinanceGoalDto.getName());
        }
        if(updatedFinanceGoalDto.getDeadline() != null){
            financeGoalModel.setDeadline(updatedFinanceGoalDto.getDeadline());
        }
        if(updatedFinanceGoalDto.getCurrentAmount() != null){
            financeGoalModel.setCurrentAmount(updatedFinanceGoalDto.getCurrentAmount());
        }
        if(updatedFinanceGoalDto.getTargetAmount() != null){
            financeGoalModel.setTargetAmount(updatedFinanceGoalDto.getTargetAmount());
        }
        if(updatedFinanceGoalDto.getStatus() != null){
            financeGoalModel.setStatus(updatedFinanceGoalDto.getStatus());
        }
        financeGoalRepository.saveAndFlush(financeGoalModel);
        FinanceGoalDtoFactory.makeFinanceGoalDto(financeGoalModel);
    }

    @Override
    @Transactional
    public void completeFinanceGoal(Long financeGoalId){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalModel.setStatus(FinanceGoalStatus.COMPLETED);
        FinanceGoalDtoFactory.makeFinanceGoalDto(financeGoalModel);
    }

    @Override
    @Transactional
    public void unrealizeFinanceGoal(Long financeGoalId){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalModel.setStatus(FinanceGoalStatus.PROGRESS);
        FinanceGoalDtoFactory.makeFinanceGoalDto(financeGoalModel);
    }

    @Override
    public List<FinanceGoalDto> getCompletedFinanceGoals(){
        Long currentUserId = userService.getCurrentUserId();
        return financeGoalRepository.findAll().stream()
                .map(FinanceGoalDtoFactory::makeFinanceGoalDto)
                .filter(financeGoalDto -> financeGoalDto.getUserId().equals(currentUserId)
                && financeGoalDto.getStatus().equals(FinanceGoalStatus.COMPLETED))
                .toList();
    }

    @Override
    public List<FinanceGoalDto> getUnrealizedFinanceGoals(){
        Long currentUserId = userService.getCurrentUserId();
        return financeGoalRepository.findAll().stream()
                .map(FinanceGoalDtoFactory::makeFinanceGoalDto)
                .filter(financeGoalDto -> financeGoalDto.getUserId().equals(currentUserId)
                && financeGoalDto.getStatus().equals(FinanceGoalStatus.PROGRESS))
                .toList();
    }

    @Override
    public void deleteFinanceGoal(Long financeGoalId){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalRepository.delete(financeGoalModel);
    }

    @Override
    public void deleteAllFinanceGoals(){
        Long currentUserId = userService.getCurrentUserId();
        List<FinanceGoalModel> financeGoalModels = financeGoalRepository.findAll().stream()
                .filter(financeGoalModel -> financeGoalModel.getUserId().equals(currentUserId))
                .toList();
        financeGoalRepository.deleteAll(financeGoalModels);
    }

//    @Override
//    @Transactional
//    public TransactionDto incomeFinanceGoal(Long financeGoalId, TransactionDto transactionDto ){
//        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
//        if(transactionDto.getAccountId()!= null){
//            AccountModel accountModel = accountService.getAccountForCurrentUser(transactionDto.getAccountId());
//            if(financeGoalModel.getCurrentAmount().compareTo(financeGoalModel.getTargetAmount()) >= 0){
//                financeGoalModel.setStatus(FinanceGoalStatus.COMPLETED);
//            }
//            financeGoalRepository.saveAndFlush(financeGoalModel);
//            return transactionService.createExpenseTransaction(
//                    transactionDto.getAccountId(),
//                    transactionDto,
//                    financeGoalId
//            );
//        }else {
//            financeGoalModel.setCurrentAmount(
//                    financeGoalModel
//                            .getCurrentAmount()
//                            .add(transactionDto.getAmount())
//            );
//            if(financeGoalModel.getCurrentAmount().compareTo(financeGoalModel.getTargetAmount()) >= 0){
//                financeGoalModel.setStatus(FinanceGoalStatus.COMPLETED);
//            }
//            financeGoalRepository.saveAndFlush(financeGoalModel);
//            return transactionService.createIncomeFinanceGoalTransaction(
//                    transactionDto,
//                    financeGoalId
//            );
//        }
//    }

    @Override
    public Double percentCompleted(Long financeGoalId){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        BigDecimal targetAmount = financeGoalModel.getTargetAmount();
        BigDecimal currentAmount = financeGoalModel.getCurrentAmount();
        return currentAmount.divide(targetAmount, 2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public FinanceGoalModel getFinanceGoalForCurrentUser(Long financeGoalId){
        Long currentUserId = userService.getCurrentUserId();
        FinanceGoalModel financeGoalModel = financeGoalRepository.findById(financeGoalId).orElseThrow(
                () -> new NotFoundException("Finance goal not found!"));
        if(!financeGoalModel.getUserId().equals(currentUserId)){
            throw new AccessDeniedException("You don't have access!");
        }
        return financeGoalModel;
    }

    @Override
    public void increaseFinanceGoal(Long financeGoalId, BigDecimal amount){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalModel.setCurrentAmount(
                financeGoalModel
                        .getCurrentAmount()
                        .add(amount)
        );
    }

    @Override
    public void decreaseFinanceGoal(Long financeGoalId, BigDecimal amount){
        FinanceGoalModel financeGoalModel = getFinanceGoalForCurrentUser(financeGoalId);
        financeGoalModel.setCurrentAmount(
                financeGoalModel
                        .getCurrentAmount()
                        .subtract(amount)
        );
    }

}
