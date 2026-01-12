package com.fintime.fintime.Factories;


import com.fintime.fintime.DTO.UserDto;
import com.fintime.fintime.Models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserDtoFactory {

    public static UserDto makeUserDto(UserModel userModel){
        return UserDto.builder()
                .id(userModel.getId())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .countAccounts(userModel.getCountAccounts())
                .build();
    }
}
