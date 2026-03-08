package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "{validation.user.name.required}")
    private String username;

    @NotBlank(message = "{validation.user.password.required}")
    private String password;

}
