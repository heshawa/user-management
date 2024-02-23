package com.acterio.users.userManagement.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.acterio.users.userManagement.api.response.CommonResponse;
import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.LoginRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;

public interface UserService {
    CommonResponse getUser(String userId);

    User getUserWithPassword(String userId);
    
    List<UserDTO> getAllUsers();

    UserDTO addUser(CreateUserRequestDTO user) throws Exception;
    
    Map<String, Integer> getDomainWiseUserCount();

    CommonResponse deleteUser(String username);

    ResponseEntity<?> login(LoginRequestDTO credentials);
    
}
