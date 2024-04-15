package com.example.lbuauth.dtos;

import lombok.Data;

@Data
public class UserDto {

    private String userId;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
