package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.CreditModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<CreditModel, Long> {

}
