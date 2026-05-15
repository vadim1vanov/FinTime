package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.DepositModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositModel, Long> {
}
