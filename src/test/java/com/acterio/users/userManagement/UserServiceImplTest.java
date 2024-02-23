package com.acterio.users.userManagement;

import com.acterio.users.userManagement.api.response.CommonResponse;
import com.acterio.users.userManagement.dto.CreateUserRequestDTO;
import com.acterio.users.userManagement.dto.UserDTO;
import com.acterio.users.userManagement.model.User;
import com.acterio.users.userManagement.persistence.dao.UserDAO;
import com.acterio.users.userManagement.security.ActerioAuthenticationProvider;
import com.acterio.users.userManagement.security.JwtUtil;
import com.acterio.users.userManagement.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActerioAuthenticationProvider authenticator;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUser() {
        // Implement test for getUser method
    }

    @Test
    void testGetUserWithPassword() {
        // Implement test for getUserWithPassword method
    }

    @Test
    void testAddUser() throws Exception {
        // Mocking necessary objects
        CreateUserRequestDTO userDTO = new CreateUserRequestDTO();
        userDTO.setEmailAddress("test@acterio.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setUsername("testUser");
        userDTO.setPassword("password");
        User user = new User();
        user.setUserId("testUser");

        when(validator.validate(any(User.class))).thenReturn(Collections.emptySet());
        when(userDAO.save(any(User.class))).thenReturn(user);

        // Calling the method and verifying the result
        UserDTO result = userService.addUser(userDTO);
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testAddUserWithValidationViolation() throws Exception {
        CreateUserRequestDTO userDTO = new CreateUserRequestDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword(""); // Empty password to cause a validation violation
        User user = new User();
        user.setUserId("testUser");

        // Mocking validation violations
        Set<ConstraintViolation<User>> violations = Collections.singleton(mock(ConstraintViolation.class));
        when(validator.validate(any(User.class))).thenReturn(violations);

        // Calling the method and verifying the result
        assertThrows(Exception.class, () -> userService.addUser(userDTO));    
    }

    @Test
    void testGetDomainWiseUserCount() {
        List<Object[]> domainCounts = Arrays.asList(
                new Object[]{"acterio.com", 2},
                new Object[]{"gmail.com", 1}
        );
        when(userDAO.countUsersByDomain()).thenReturn(domainCounts);

        // Calling the method
        Map<String, Integer> domainUserCountMap = userService.getDomainWiseUserCount();

        // Verifying the result
        assertEquals(2, domainUserCountMap.size());
        assertEquals(5, domainUserCountMap.get("domain1"));
        assertEquals(10, domainUserCountMap.get("domain2"));    
    }

    @Test
    void testDeleteUser() {
        String username = "testUser";
        User user = new User();
        user.setUserId(username);

        // Mocking behavior of UserDAO
        when(userDAO.getOne(username)).thenReturn(user);
        doNothing().when(userDAO).deleteById(username);

        // Act
        CommonResponse response = userService.deleteUser(username);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("User " + username + " deleted successfully", response.getMessage().toString());
    }

    @BeforeEach
    void setUp() {
        // Create dummy users
        Object[] user1 = new Object[]{"user1", "user1@acterio.com", "userA", "User"};
        Object[] user2 = new Object[]{"user2", "user2@acterio.com", "userB", "User"};
        Object[] user3 = new Object[]{"user3", "user3@gmail.com", "userC", "User"};

        List<Object[]> dummyUsers = Arrays.asList(user1, user2, user3);

        // Mock userDAO to return dummy users
        when(userDAO.countUsersByDomain()).thenReturn(dummyUsers);
    }

}
