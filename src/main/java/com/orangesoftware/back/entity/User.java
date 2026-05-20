package com.orangesoftware.back.entity;

import lombok.Data;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private String email;
}
