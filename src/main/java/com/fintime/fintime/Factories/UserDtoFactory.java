package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.UserDto;
import com.fintime.fintime.Models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserDtoFactory {

    public UserDto makeUserDto(UserModel userModel){
        return UserDto.builder()
                .id(userModel.getId())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .email(userModel.getEmail())
                .password(userModel.getPassword())
                .createdAt(userModel.getCreatedAt())
                .build();
    }
}
