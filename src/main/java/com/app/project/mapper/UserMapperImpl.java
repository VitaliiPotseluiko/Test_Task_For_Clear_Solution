package com.app.project.mapper;

import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper<UserRequestDto, User, UserResponseDto> {
    @Override
    public User toModel(UserRequestDto requestDto) {
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setBirthDate(requestDto.getBirthDate());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return user;
    }

    @Override
    public UserResponseDto toDto(User model) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(model.getId());
        responseDto.setEmail(model.getEmail());
        responseDto.setFirstName(model.getFirstName());
        responseDto.setLastName(model.getLastName());
        responseDto.setBirthDate(model.getBirthDate());
        responseDto.setPhoneNumber(model.getPhoneNumber());
        responseDto.setAddress(model.getAddress());
        return responseDto;
    }
}
