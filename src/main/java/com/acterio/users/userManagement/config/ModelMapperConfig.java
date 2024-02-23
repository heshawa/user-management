package com.acterio.users.userManagement.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.createTypeMap(User.class, UserDTO.class)
                .addMapping(User::getUserId, UserDTO::setUsername);
        mapper.createTypeMap(CreateUserRequestDTO.class, User.class)
                .addMapping(UserDTO::getUsername, User::setUserId);
        return mapper;
    }
}
