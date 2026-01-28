package com.fintime.fintime.Services.Impl;

import com.fintime.fintime.DTO.UserDto;
import com.fintime.fintime.Exceptions.BadRequestException;
import com.fintime.fintime.Exceptions.NotFoundException;
import com.fintime.fintime.Factories.UserDtoFactory;
import com.fintime.fintime.Enums.Role;
import com.fintime.fintime.Models.UserModel;
import com.fintime.fintime.Repository.UserRepository;
import com.fintime.fintime.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDtoFactory userDtoFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    @Override
    public void createUser(String firstName, String lastName, String username, String password){
        String hashedPassword = passwordEncoder.encode(password);
        userRepository.findByUsername(username).ifPresent(email -> {
            throw new BadRequestException("Аккаунт с таким email уже создан!");
        });
        UserModel user = userRepository.saveAndFlush(
                UserModel.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(hashedPassword)
                        .username(username)
                        .role(Role.USER)
                        .countAccounts(0L)
                        .createdAt(Instant.now())
                        .build()
        );
        UserDtoFactory.makeUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers(){
       return userRepository.findAll().stream()
                .map(UserDtoFactory::makeUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        UserModel user = userRepository.findById(userId).orElseThrow(
                () ->  new BadRequestException("User with id " + userId + " not exist!" )
        );
        if(userDto.getFirstName() != null){
            user.setUsername(userDto.getUsername());
        }
        if(userDto.getLastName() != null){
            user.setLastName(userDto.getLastName());
        }
        if(userDto.getUsername() != null){
            user.setUsername(userDto.getUsername());
        }

        UserModel updatedUser = userRepository.saveAndFlush(user);
        return UserDtoFactory.makeUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId){
        UserModel user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("User with id " + userId + " not exist!")
        );
        userRepository.delete(user);
    }

    @Override
    public Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserModel current_user = userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User not found!")
        );
        return current_user.getId();
    }




}
