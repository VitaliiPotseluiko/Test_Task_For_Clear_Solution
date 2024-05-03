package com.app.project.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserRequestDto {
    private String firstName;
    private String lastName;
}
