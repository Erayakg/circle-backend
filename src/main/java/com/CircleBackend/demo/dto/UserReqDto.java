package com.CircleBackend.demo.dto;

import lombok.Data;

@Data
public class UserReqDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String UserId;
    private String role;
}
