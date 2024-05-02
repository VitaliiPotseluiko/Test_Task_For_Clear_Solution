package com.app.project.dto;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StatusErrorDto {
    private String error;
    private LocalDateTime timeStamp;
    private HttpStatus status;
}
