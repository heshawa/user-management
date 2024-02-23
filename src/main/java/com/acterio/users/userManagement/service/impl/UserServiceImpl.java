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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;
import com.acterio.users.userManagement.persistence.dao.UserDAO;
import com.acterio.users.userManagement.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final LocalValidatorFactoryBean validator;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserDAO userDAO, ModelMapper modelMapper, LocalValidatorFactoryBean validator, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUser(String userId){
        User user = getUserWithPassword(userId);
        if (user == null) {
            log.error("Cannot find the user. username="+userId);
            return null;
        }
        log.info("USer received successfully. userId="+userId);
        return modelMapper.map(user, UserDTO.class);
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
            throw new Exception("User detail validation error");
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
    public void deleteUser(String username) {
        userDAO.deleteById(username);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return null;
    }
}
