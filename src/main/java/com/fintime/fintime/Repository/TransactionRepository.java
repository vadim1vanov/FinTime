package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    @Modifying
    @Query(value = "DELETE FROM transactions WHERE account_id = :accountId", nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);
}
