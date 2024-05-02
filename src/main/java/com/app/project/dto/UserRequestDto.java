package com.app.project.dto;

import com.app.project.validation.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRequestDto {
    @Email
    private String email;
    @NotBlank(message = "can't be blank")
    private String firstName;
    @NotBlank(message = "can't be blank")
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
}
