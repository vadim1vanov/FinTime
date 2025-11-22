package com.fintime.fintime.Repository;


import com.fintime.fintime.Models.CurrenciesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrenciesRepository extends JpaRepository<CurrenciesModel, Long> {
}
