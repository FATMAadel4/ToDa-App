package com.example.Todo.Service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTokenResponse {
    private Long userId;
    private String email;
    private String role;
}

