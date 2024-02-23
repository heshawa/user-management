package com.acterio.users.userManagement.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    public LoginResponse(String jwt) {
        this.token = jwt;
    }
}
