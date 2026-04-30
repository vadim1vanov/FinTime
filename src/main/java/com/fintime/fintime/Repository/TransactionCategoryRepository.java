package com.fintime.fintime.Repository;

import com.fintime.fintime.Enums.TransactionType;
import com.fintime.fintime.Models.TransactionCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategoryModel,Long > {
    Optional<TransactionCategoryModel> findCustomTransactionCategoryByName(String customTransactionCategoryName);

    @Query(value = "SELECT * FROM transaction_category c WHERE " +
            "(c.category_scope = 'GLOBAL' OR c.user_id = :userId) AND c.transaction_type = :transactionType",
    nativeQuery = true)
    List<TransactionCategoryModel> getAllTransactionCategory(@Param("userId") Long userId,
                                                             @Param("transactionType") String transactionType);
}
