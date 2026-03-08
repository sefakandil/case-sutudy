package com.example.demo.auth.dto;

import com.example.demo.auth.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    private String password;

    private Role role;

}
