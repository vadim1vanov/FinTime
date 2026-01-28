package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    Optional<AccountModel> findByAccountName(String accountName);
    @Modifying
    @Query("UPDATE AccountModel a SET a.accountPosition = a.accountPosition + 1 WHERE a.userId = :userId")
    void shiftPositionsDown(@Param("userId") Long userId);

}
