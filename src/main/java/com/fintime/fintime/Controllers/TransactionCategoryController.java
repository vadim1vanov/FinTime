package com.fintime.fintime.Controllers;

import com.fintime.fintime.DTO.TransactionCategoryDto;
import com.fintime.fintime.Services.TransactionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/transationcategory")
@RequiredArgsConstructor
public class TransactionCategoryController {
    private final TransactionCategoryService transactionCategoryService;
//    @GetMapping
//    public List<TransactionCategoryDto> getIncomeTransactionCategories(){
//        return transactionCategoryService.getIncomeTransactionCategories();
//    }
    @GetMapping("/income")
    public  List<TransactionCategoryDto> getAllIncomeTransactionCategory(){
        return transactionCategoryService.getAllIncomeTransactionCategory();
    }
    @GetMapping("/expense")
    public  List<TransactionCategoryDto> getAllExpenseTransactionCategory(){
        return transactionCategoryService.getAllExpenseTransactionCategory();
    }

    @PostMapping
    public ResponseEntity<TransactionCategoryDto> createTransactionCategory(
            @RequestBody TransactionCategoryDto transactionCategoryDto){
        TransactionCategoryDto transactionCategory = transactionCategoryService.createTransactionCategory(transactionCategoryDto);
        return ResponseEntity.created(URI.create("api/transactioncategory" + transactionCategory.getId()))
                .body(transactionCategory);
    }
}
