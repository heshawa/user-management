package com.acterio.users.userManagement.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.acterio.users.userManagement.api.response.CommonResponse;
import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.LoginRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;
import com.acterio.users.userManagement.persistence.dao.UserDAO;
import com.acterio.users.userManagement.security.ActerioAuthenticationProvider;
import com.acterio.users.userManagement.security.JwtUtil;
import com.acterio.users.userManagement.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final LocalValidatorFactoryBean validator;
    private final PasswordEncoder passwordEncoder;
    private final ActerioAuthenticationProvider authenticator;
    private final JwtUtil jwtUtil;


    @Autowired
    public UserServiceImpl(UserDAO userDAO, ModelMapper modelMapper, LocalValidatorFactoryBean validator, PasswordEncoder passwordEncoder, ActerioAuthenticationProvider authenticator, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.authenticator = authenticator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public CommonResponse getUser(String userId){
        CommonResponse<UserDTO> userResponse = new CommonResponse();
        User user = null;
        try {
            user = getUserWithPassword(userId);
            if (user == null) {
                log.error("Cannot find the user. username="+userId);
                userResponse.setMessage("User not found");
                return userResponse;
            }
            log.info("USer received successfully. userId="+userId);
        } catch (Exception e) {
            log.error("Error while finding the user.",e);
            userResponse.setMessage("User not found");
            return userResponse;
        }
        if(user != null){
            userResponse.setSuccess(true);
            userResponse.setObject(modelMapper.map(user,UserDTO.class));
        }
        return userResponse;
    }

    @Override
    public User getUserWithPassword(String userId) {
        return userDAO.findById(userId).orElse(null);
    }

    @Override
    public UserDTO addUser(CreateUserRequestDTO userDTO) throws Exception {
        User user = modelMapper.map(userDTO,User.class);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User savedUser = null;
        if(violations.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            savedUser = userDAO.save(user);
        }else{
            ConstraintViolation<User> passwordViolation = violations.stream()
                    .filter(violation -> violation.getPropertyPath().toString().equals("password"))
                    .findFirst().orElse(null);
            if(passwordViolation != null && StringUtils.isEmpty(user.getPassword())){
                user.setPassword(userDAO.getOne(user.getUserId()).getPassword());
                savedUser = userDAO.save(user);
            } else{
                throw new Exception("User detail validation error");
            }
        }
        return modelMapper.map(savedUser,UserDTO.class);
    }

    @Override
    public Map<String, Integer> getDomainWiseUserCount() {
        List<Object[]> domainCounts = userDAO.countUsersByDomain();
        Map<String, Integer> domainUserCountMap = new HashMap();

        for (Object[] row : domainCounts) {
            String domain = (String) row[0];
            Integer count = ((Number) row[1]).intValue(); // Convert to Integer
            domainUserCountMap.put(domain, count);
        }

        return domainUserCountMap;
    }

    @Override
    public CommonResponse deleteUser(String username) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            User user = userDAO.getOne(username);
            if (user != null) {
                userDAO.deleteById(username);
                response.setMessage("User " + username + " deleted successfully");
                response.setSuccess(true);
                return response;
            } else {
                response.setMessage("User " + username + " not found");
                return response;
            }
        } catch (Exception e) {
            response.setMessage("Failed to delete user " + username);
            return response;
        }
    }

    @Override
    public ResponseEntity<?> login(LoginRequestDTO credentials) {
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

    @Override
    public List<UserDTO> getAllUsers() {
        return null;
    }
}
