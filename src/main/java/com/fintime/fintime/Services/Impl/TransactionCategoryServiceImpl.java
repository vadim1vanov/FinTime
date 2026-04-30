package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.TransactionCategoryDto;
import com.fintime.fintime.Enums.TransactionType;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.TransactionCategoryDtoFactory;
import com.fintime.fintime.Models.TransactionCategoryModel;
import com.fintime.fintime.Repository.TransactionCategoryRepository;
import com.fintime.fintime.Services.TransactionCategoryService;
import com.fintime.fintime.Services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    TransactionCategoryRepository transactionCategoryRepository;
    UserService userService;
    @Override
    public TransactionCategoryDto createTransactionCategory(TransactionCategoryDto transactionCategoryDto){
        Long currentUserId = userService.getCurrentUserId();
        if(transactionCategoryRepository
                .findCustomTransactionCategoryByName(transactionCategoryDto.getCategoryName()).isPresent()){
            throw new BadRequestException("Category already created!");
        }
        TransactionCategoryModel transactionCategoryModel = transactionCategoryRepository
                .saveAndFlush(
                        TransactionCategoryModel.builder()
                                .userId(currentUserId)
                                .name(transactionCategoryDto.getCategoryName())
                                .transactionType(transactionCategoryDto.getTransactionType())
                                .categoryScope("CUSTOM")
                                .build()
                );
        return TransactionCategoryDtoFactory.makeTransactionCategoryDto(transactionCategoryModel);
    }

    @Override
    public TransactionCategoryModel getTransactionCategory(Long transactionCategoryId){
        Long currentUserId = userService.getCurrentUserId();
        TransactionCategoryModel transactionCategoryModel = transactionCategoryRepository
                .findById(transactionCategoryId).orElseThrow(
            () -> new NotFoundException("Custom transaction category not found!"));
        if(!transactionCategoryModel.getUserId().equals(currentUserId)){
            throw new AccessDeniedException("You don't have access!");
        }
        return transactionCategoryModel;
    }

    @Override
    public void editTransactionCategory(Long transactionCategoryId,
                                              TransactionCategoryDto updatedTransactionCategoryDto){
        TransactionCategoryModel transactionCategoryModel = getTransactionCategory(transactionCategoryId);
        if(updatedTransactionCategoryDto.getCategoryName() != null
                && !updatedTransactionCategoryDto.getCategoryName().isBlank()){
            transactionCategoryModel.setName(updatedTransactionCategoryDto.getCategoryName());
        }
        if(updatedTransactionCategoryDto.getTransactionType() != null){
            transactionCategoryModel.setTransactionType(updatedTransactionCategoryDto.getTransactionType());
        }
        transactionCategoryRepository.saveAndFlush(transactionCategoryModel);
    }

//    @Override
//    public List<TransactionCategoryDto> getIncomeTransactionCategories(){
//        return transactionCategoryRepository.findAll().stream()
//                .map(TransactionCategoryDtoFactory::makeTransactionCategoryDto)
//                .filter(transactionCategoryDto ->
//                        transactionCategoryDto.getTransactionType().equals(TransactionType.INCOME))
//                .toList();
//    }

    @Override
    public List<TransactionCategoryDto> getIncomeTransactionCategories(Long userId){
        Long currentUserId = userService.getCurrentUserId();
        return transactionCategoryRepository.findAll().stream()
                .map(TransactionCategoryDtoFactory::makeTransactionCategoryDto)
                .filter(transactionCategoryDto ->
                        transactionCategoryDto.getTransactionType().equals(TransactionType.INCOME) &&
                        transactionCategoryDto.getUserId().equals(currentUserId))
                .toList();
    }

    @Override
    public List<TransactionCategoryDto> getAllIncomeTransactionCategory(){
        Long currentUserId = userService.getCurrentUserId();
        return transactionCategoryRepository.getAllTransactionCategory(currentUserId, TransactionType.INCOME.name()).stream()
                .map(TransactionCategoryDtoFactory::makeTransactionCategoryDto)
                .toList();
    }

    @Override
    public List<TransactionCategoryDto> getAllExpenseTransactionCategory(){
        Long currentUserId = userService.getCurrentUserId();
        return transactionCategoryRepository.getAllTransactionCategory(currentUserId, TransactionType.EXPENSE.name()).stream()
                .map(TransactionCategoryDtoFactory::makeTransactionCategoryDto)
                .toList();
    }








}
