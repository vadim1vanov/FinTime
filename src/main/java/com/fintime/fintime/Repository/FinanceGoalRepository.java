package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.FinanceGoalModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceGoalRepository extends JpaRepository<FinanceGoalModel, Long> {
}
