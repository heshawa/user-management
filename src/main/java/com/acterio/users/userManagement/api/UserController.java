package com.acterio.users.userManagement.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.acterio.users.userManagement.api.response.CommonResponse;
import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.LoginRequestDTO;
import com.acterio.users.userManagement.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequestDTO user){
        return ResponseEntity.ok(userService.saveOrUpdateUser(user, UserService.ActionType.CREATE));
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody CreateUserRequestDTO user){
        return ResponseEntity.ok(userService.saveOrUpdateUser(user, UserService.ActionType.UPDATE));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        CommonResponse<String> response = userService.deleteUser(username);
        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        CommonResponse userResponse = userService.getUser(userId);
        if(userResponse.isSuccess()){
            return ResponseEntity.ok(userResponse);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResponse);
        }
    }
    
    @GetMapping("/domain")
    public ResponseEntity<?> getEmailDomain(){
        Map<String, Integer> domainWiseUserCount = userService.getDomainWiseUserCount();
        if(domainWiseUserCount.isEmpty()){
            CommonResponse response = new CommonResponse();
            response.setMessage("There are no users in the system");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(domainWiseUserCount);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
        return userService.login(credentials);
    }
}
