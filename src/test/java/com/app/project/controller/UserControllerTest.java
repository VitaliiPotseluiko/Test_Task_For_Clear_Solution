package com.app.project.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.project.dto.StatusErrorDto;
import com.app.project.dto.UpdateUserRequestDto;
import com.app.project.dto.UserRequestDto;
import com.app.project.dto.UserResponseDto;
import com.app.project.mapper.UserMapper;
import com.app.project.mapper.UserMapperImpl;
import com.app.project.model.User;
import com.app.project.storage.Storage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;
import java.time.Month;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private static final String USERS_PATH = "/users";
    private static final int USERS_NUMBERS = 9;
    private static final Long WRONG_ID = 30L;
    private final UserMapper<UserRequestDto, User, UserResponseDto> mapper =
            new UserMapperImpl();
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

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
    public void registerUser_createUser_Success() throws Exception {
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
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(USERS_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
    }

    @Test
    public void updateUser_updateAllUsersFields_Success() throws Exception {
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
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_NotExistingId_NotFound() throws Exception {
        Long id = WRONG_ID;
        UserRequestDto requestDto = new UserRequestDto()
                .setEmail("User90@gmail.com")
                .setLastName("lastName")
                .setFirstName("firstName")
                .setBirthDate(LocalDate.of(1999, Month.APRIL, 4));
        String expected = "Can't find user by id = " + id;
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }

    @Test
    public void updateUser_updateTwoUserFields_Success() throws Exception {
        Long id = 4L;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto()
                .setFirstName("newUserFirstName")
                .setLastName("newUserLastName");
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail("User4@gmail.com")
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setBirthDate(LocalDate.of(1940, Month.APRIL, 4));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_NotExistingId_NotOk() throws Exception {
        Long id = WRONG_ID;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto()
                .setLastName("newUserFirstName")
                .setFirstName("newUserLastName");
        String expected = "Can't find user by id = " + id;
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }

    @Test
    public void deleteUser_WithExistingId_Ok() throws Exception {
        Long id = 6L;
        UserResponseDto expected = new UserResponseDto()
                .setId(id)
                .setEmail("User6@gmail.com")
                .setFirstName("UserFirstName6")
                .setLastName("UserLastName6")
                .setBirthDate(LocalDate.of(1960, Month.APRIL, 6));

        MvcResult result = mockMvc.perform(delete(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
        assertEquals(8, Storage.storage.size());
    }

    @Test
    public void deleteUser_NotExistingId_NotFound() throws Exception {
        Long id = WRONG_ID;
        String expected = "Can't find user by id = " + id;

        MvcResult result = mockMvc.perform(delete(USERS_PATH.concat("/").concat(String.valueOf(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }

    @Test
    public void getAllUsersByRange_AllUsersInRange_Ok() throws Exception {
        LocalDate from = LocalDate.of(1950, Month.JANUARY, 1);
        LocalDate to = LocalDate.of(1980, Month.JANUARY, 1);
        UserResponseDto[] expected = Storage.storage.values().stream()
                .filter(u -> u.getBirthDate().isAfter(from) && u.getBirthDate().isBefore(to))
                .map(mapper::toDto)
                .toArray(UserResponseDto[]::new);

        MvcResult result = mockMvc.perform(get(USERS_PATH.concat("/range?from=")
                                .concat(from.toString())
                                .concat("&to=")
                                .concat(to.toString())
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto[].class);

        assertEquals(expected.length, actual.length);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getAllUsersByRange_AllUsersInRange_BadRequest() throws Exception {
        LocalDate from = LocalDate.of(2000, Month.JANUARY, 1);
        LocalDate to = LocalDate.of(1980, Month.JANUARY, 1);
        String expected = "Argument 'from' must be greater than 'to'";

        MvcResult result = mockMvc.perform(get(USERS_PATH.concat("/range?from=")
                                .concat(from.toString())
                                .concat("&to=")
                                .concat(to.toString())
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }
}