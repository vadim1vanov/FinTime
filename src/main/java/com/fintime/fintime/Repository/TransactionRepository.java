package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {
}
