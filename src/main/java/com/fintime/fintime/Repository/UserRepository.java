package com.fintime.fintime.Repository;

import com.fintime.fintime.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {
}
