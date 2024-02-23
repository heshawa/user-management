package com.acterio.users.userManagement.service;

import java.util.List;
import java.util.Map;

import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;

public interface UserService {
    UserDTO getUser(String userId);

    User getUserWithPassword(String userId);
    
    List<UserDTO> getAllUsers();

    UserDTO addUser(CreateUserRequestDTO user) throws Exception;
    
    Map<String, Integer> getDomainWiseUserCount();

    void deleteUser(String username);
}
