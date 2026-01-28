package com.fintime.fintime.Services;

import com.fintime.fintime.DTO.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    void createUser(String firstName, String lastName, String username, String password);
    List<UserDto> getAllUsers();
    UserDto editUser(Long userId, UserDto userDto);
    void deleteUser(Long userId);
    Long getCurrentUserId();
}


