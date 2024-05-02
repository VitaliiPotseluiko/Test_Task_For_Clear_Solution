package com.app.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.app.project.dto.UpdateUserRequestDto;
import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.exception.EntityNotFoundException;
import com.app.project.exception.IncorrectArgumentException;
import com.app.project.mapper.UserMapper;
import com.app.project.mapper.UserMapperImpl;
import com.app.project.model.User;
import com.app.project.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

class UserServiceImplTest {
    private static final int USERS_NUMBERS = 9;
    private static final Long WRONG_ID = 20L;
    private final UserMapper<UserRequestDto, User, UserResponseDto> mapper = new UserMapperImpl();
    private final UserService userService = new UserServiceImpl(mapper);

    @BeforeEach
     void setUp() {
        for (int i = 1; i <= USERS_NUMBERS; i++) {
            String email = "User"+ i + "@gmail.com";
            String firstName = "UserFirstName" + i;
            String lastName = "UserLastName" + i;
            String year = "19" + i + "0";
            LocalDate birthDate = LocalDate.of(Integer.parseInt(year), Month.APRIL, i);
            User user = new User();
            user.setId((long) i);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBirthDate(birthDate);
            Storage.storage.put((long)i, user);
        }
    }

    @Test
    public void create_createUser_Ok() {
        Long id = 10L;
        UserRequestDto requestDto = new UserRequestDto()
                .setEmail("User12@gmail.com")
                .setFirstName("firstName12")
                .setLastName("lastName12")
                .setBirthDate(LocalDate.of(1989, Month.APRIL, 17));
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setBirthDate(requestDto.getBirthDate());

        UserResponseDto actual = userService.create(requestDto);

        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
    }

    @Test
    public void update_updateAllFields_Ok() {
        Long id = 3L;
        UserRequestDto requestDto = new UserRequestDto()
                .setEmail("User12@gmail.com")
                .setFirstName("firstName12")
                .setLastName("lastName12")
                .setBirthDate(LocalDate.of(1989, Month.APRIL, 17));
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setBirthDate(requestDto.getBirthDate());

        UserResponseDto actual = userService.update(id, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    public void update_updateAllFields_NotOk() {
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.update(WRONG_ID, new UserRequestDto()));

        assertEquals("EntityNotFoundException", actual.getClass().getSimpleName());
        assertEquals("Can't find user by id = 20", actual.getMessage());
    }

    @Test
    public void update_updateTwoFields_Ok() {
        Long id = 5L;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto()
                .setFirstName("newFirstName")
                .setLastName("newLastName");
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail(Storage.storage.get(id).getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setBirthDate(Storage.storage.get(id).getBirthDate());

        UserResponseDto actual = userService.update(id, requestDto);

        assertEquals(expected, actual);
        assertEquals(requestDto.getFirstName(), Storage.storage.get(id).getFirstName());
        assertEquals(requestDto.getLastName(), Storage.storage.get(id).getLastName());
    }

    @Test
    public void update_updateTwoFields_NotOk() {
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.update(WRONG_ID, new UpdateUserRequestDto()));

        assertEquals("EntityNotFoundException", actual.getClass().getSimpleName());
        assertEquals("Can't find user by id = 20", actual.getMessage());
    }

    @Test
    public void update_deleteUser_NotOk() {
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteById(WRONG_ID));

        assertEquals("EntityNotFoundException", actual.getClass().getSimpleName());
        assertEquals("Can't find user by id = 20", actual.getMessage());
    }

    @Test
    public void update_deleteUser_Ok() {
        Long id = 4L;
        int expectedSize = Storage.storage.size() - 1;
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail("User4@gmail.com")
                .setFirstName("UserFirstName4")
                .setLastName("UserLastName4")
                .setBirthDate(LocalDate.of(1940, Month.APRIL, 4));

        UserResponseDto actual = userService.deleteById(id);

        assertEquals(expected, actual);
        assertEquals(expectedSize, Storage.storage.size());
    }

    @Test
    public void findAllByRange_FindAllUsersByCertainDateRange_Ok() {
        LocalDate from = LocalDate.of(1950, Month.JANUARY, 1);
        LocalDate to = LocalDate.of(1980, Month.JANUARY, 1);
        List<UserResponseDto> expected = Storage.storage.values().stream()
                .filter(u -> u.getBirthDate().isAfter(from) && u.getBirthDate().isBefore(to))
                .map(mapper::toDto)
                .toList();

        List<UserResponseDto> actual = userService.findAllByRange(from, to);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void findAllByRange_FindAllUsersByCertainDateRange_NotOk() {
        LocalDate from = LocalDate.of(2000, Month.JANUARY, 1);
        LocalDate to = LocalDate.of(1980, Month.JANUARY, 1);

        Exception actual = assertThrows(IncorrectArgumentException.class,
                () -> userService.findAllByRange(from, to));

        assertEquals("IncorrectArgumentException", actual.getClass().getSimpleName());
        assertEquals("Argument 'from' must be greater than 'to'", actual.getMessage());
    }

}