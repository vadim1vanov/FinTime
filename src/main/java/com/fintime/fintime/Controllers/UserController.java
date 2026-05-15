package com.fintime.fintime.Controllers;


import com.fintime.fintime.DTO.UserDto;
import com.fintime.fintime.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    @PostMapping()
    public ResponseEntity<Void> registerUser(@ModelAttribute UserDto userDto, HttpServletRequest request) {
        userService.createUser(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getUsername(),
                userDto.getPassword());
        UserDetails userDetails = userService.loadUserByUsername(userDto.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Привязка SecurityContext к сессии пользователя:
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editUser(@PathVariable Long userId, @RequestBody UserDto userDto){
        userService.editUser(userId, userDto);
    }

    @GetMapping
    public UserDto getUserInfo(){
        return userService.getUserInfo();
    }




}
