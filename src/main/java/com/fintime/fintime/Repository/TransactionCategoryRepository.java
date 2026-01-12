package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.TransactionCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategoryModel,Long > {
    Optional<TransactionCategoryModel> findCustomTransactionCategoryByName(String customTransactionCategoryName);
}
