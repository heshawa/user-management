package com.acterio.users.userManagement.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.acterio.users.userManagement.api.response.CommonResponse;
import com.acterio.users.userManagement.api.response.LoginResponse;
import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.LoginRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;
import com.acterio.users.userManagement.security.ActerioAuthenticationProvider;
import com.acterio.users.userManagement.security.JwtUtil;
import com.acterio.users.userManagement.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private enum ActionType {
        CREATE, UPDATE
    }

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @Autowired
    private ActerioAuthenticationProvider authenticator;

    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequestDTO user){
        return saveOrUpdateUser(user, ActionType.CREATE);
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody CreateUserRequestDTO user){
        return saveOrUpdateUser(user,ActionType.UPDATE);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            UserDTO user = userService.getUser(username);
            if (user != null) {
                userService.deleteUser(username);
                response.setMessage("User " + username + " deleted successfully");
                response.setSuccess(true);
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("User " + username + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setMessage("Failed to delete user " + username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        CommonResponse<UserDTO> userResponse = new CommonResponse();
        UserDTO user = null;
        try {
            user = userService.getUser(userId);
            if(user==null){
                userResponse.setMessage("User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResponse);
            }
        } catch (Exception e) {
            log.error("Error while finding the user.",e);
            userResponse.setMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResponse);
        }
        if(user != null){
            userResponse.setSuccess(true);
            userResponse.setObject(user);
        }
        return ResponseEntity.ok(userResponse);
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
        Authentication authentication;
        try{
            authentication = authenticator
                    .authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        }catch (Exception ex){
            log.error("User does not exist or incorrect password. username="+credentials.getUsername());
            CommonResponse response = new CommonResponse();
            response.setMessage("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        log.info("User authentication successful. user="+credentials.getUsername());
        final String jwtToken = jwtUtil.generateToken(credentials.getUsername());
        
        Map<String, String> token = new HashMap();
        token.put("token", jwtToken);
        token.put("validity", jwtUtil.extractExpiration(jwtToken).toString());
        log.info("User jwt token generated. user="+credentials.getUsername());

        return ResponseEntity.ok(token);
    }
    
    private ResponseEntity<?> saveOrUpdateUser(CreateUserRequestDTO user,ActionType action){
        CommonResponse<UserDTO> userResponse = new CommonResponse();
        try {
            if(ActionType.CREATE.equals(action) && userService.getUser(user.getUsername()) != null){
                userResponse.setMessage("User "+user.getUsername()+" is already existing");
                return ResponseEntity.ok(userResponse);
            }else if(ActionType.UPDATE.equals(action) && userService.getUser(user.getUsername()) == null){
                userResponse.setMessage("User "+user.getUsername()+" is not found");
                return ResponseEntity.ok(userResponse);
            }
            userResponse.setObject(userService.addUser(user));
            userResponse.setSuccess(true);
        } catch (Exception e) {
            userResponse.setMessage("User creation failed");
            log.error("Error while creating/updating new user.",e);

        }
        return ResponseEntity.ok(userResponse);
    }
}
