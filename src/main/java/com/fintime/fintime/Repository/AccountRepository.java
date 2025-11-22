package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    Optional<AccountModel> findByAccountName(String accountName);
}
