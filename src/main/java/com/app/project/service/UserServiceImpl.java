package com.app.project.service;

import com.app.project.dto.UpdateUserRequestDto;
import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.exception.EntityNotFoundException;
import com.app.project.exception.IncorrectArgumentException;
import com.app.project.exception.UserRegistrationException;
import com.app.project.mapper.UserMapper;
import com.app.project.model.User;
import com.app.project.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper<UserRequestDto, User, UserResponseDto> mapper;
    @Value("${acceptable_age}")
    private int acceptableAge;
    private int deletedElementNumbers = 0;

    public UserServiceImpl(UserMapper<UserRequestDto, User, UserResponseDto> mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserResponseDto create(UserRequestDto requestDto) {
        if (LocalDate.now().minusYears(acceptableAge).isAfter(requestDto.getBirthDate())
                || LocalDate.now().minusYears(acceptableAge).isEqual(requestDto.getBirthDate())) {
            User user = mapper.toModel(requestDto);
            Long id = Storage.storage.size() + 1L + deletedElementNumbers;
            user.setId(id);
            Storage.storage.put(user.getId(), user);
            return mapper.toDto(user);
        } else {
            throw new UserRegistrationException("User can't be registered, cause he is younger than 18");
        }
    }

    @Override
    public UserResponseDto getById(Long id) {
        return mapper.toDto(findUserById(id));
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        return Storage.storage.values().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> findAllByRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IncorrectArgumentException("Argument 'from' must be greater than 'to'");
        }
        return Storage.storage.values().stream()
                .filter(u -> u.getBirthDate().isAfter(from) && u.getBirthDate().isBefore(to))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        findUserById(id);
        User user = mapper.toModel(requestDto);
        user.setId(id);
        Storage.storage.put(id, user);
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto update(Long id, UpdateUserRequestDto requestDto) {
        User user = findUserById(id);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        Storage.storage.put(id, user);
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto deleteById(Long id) {
        User deletedUser = findUserById(id);
        Storage.storage.remove(id);
        deletedElementNumbers++;
        return mapper.toDto(deletedUser);
    }

    private User findUserById(Long id) {
        if (Storage.storage.containsKey(id)) {
            return Storage.storage.get(id);
        } else {
            throw new EntityNotFoundException("Can't find user by id = " + id);
        }
    }
}
