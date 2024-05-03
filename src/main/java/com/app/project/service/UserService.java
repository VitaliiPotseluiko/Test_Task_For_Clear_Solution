package com.app.project.service;

import com.app.project.dto.UpdateUserRequestDto;
import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.model.User;
import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto user);

    UserResponseDto getById(Long id);

    List<UserResponseDto> findAllUsers();

    List<UserResponseDto> findAllByRange(LocalDate from, LocalDate to);

    UserResponseDto update(Long id, UserRequestDto user);

    UserResponseDto update(Long id, UpdateUserRequestDto requestDto);

    UserResponseDto deleteById(Long id);
}
