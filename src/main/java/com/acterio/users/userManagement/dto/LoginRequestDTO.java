package com.acterio.users.userManagement.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
}
