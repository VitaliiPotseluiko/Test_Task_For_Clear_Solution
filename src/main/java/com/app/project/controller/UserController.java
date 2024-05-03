package com.app.project.controller;

import com.app.project.dto.UpdateUserRequestDto;
import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/range")
    public List<UserResponseDto> getAllUsersByRange(@RequestParam LocalDate from,
                                         @RequestParam LocalDate to) {
        return userService.findAllByRange(from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(@RequestBody @Valid UserRequestDto requestDto) {
        return userService.create(requestDto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                           @RequestBody @Valid UserRequestDto requestDto) {
        return userService.update(id, requestDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                           @RequestBody UpdateUserRequestDto requestDto) {
        return userService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id) {
        return userService.deleteById(id);
    }
}
