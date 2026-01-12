package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    @Query(value = "SELECT COUNT(*) from accounts \n" +
            "JOIN users ON users.id = accounts.user_id\n" +
            "WHERE users.id = :userId",
            nativeQuery = true)
    Long getCountAccountsByUser(@Param("userId") Long userId);
}
