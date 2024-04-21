package com.example.demo.controller;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.JwtUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private static final String TEST_USERNAME = "test_username";
    private static final String TEST_PASSWORD = "test_password";

    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtUtils jwtUtils = mock(JwtUtils.class);

    @Before
    public void setup() {
        userController = new UserController(userRepository, cartRepository, passwordEncoder, authenticationManager, jwtUtils);
    }

    @Test
    public void findByIdTest() {
        long id = 1L;
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setUsername(TEST_USERNAME);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        ResponseEntity<User> response = userController.findById(id);
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertEquals(user.getId(), id);
    }

    @Test
    public void findByNameTest() {
        User mockUser = new User();
        mockUser.setUsername(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(mockUser);

        ResponseEntity<User> response = userController.findByUserName(TEST_USERNAME);
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertEquals(user.getUsername(), TEST_USERNAME);
    }

    @Test
    public void findByNameTestFailure() {
        User mockUser = new User();
        mockUser.setUsername(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(mockUser);

        ResponseEntity<User> response = userController.findByUserName("");
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void createUserTest() {
        when(passwordEncoder.encode(anyString())).thenReturn(TEST_PASSWORD);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USERNAME);
        createUserRequest.setPassword(TEST_PASSWORD);
        createUserRequest.setConfirmPassword(TEST_PASSWORD);

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        Assert.assertNotNull(createdUser);
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertEquals(createdUser.getUsername(), TEST_USERNAME);
        Assert.assertEquals(createdUser.getPassword(), TEST_PASSWORD);
    }

    @Test
    public void createUserTestFailureOnInvalidPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USERNAME);
        createUserRequest.setPassword("123");
        createUserRequest.setConfirmPassword("123");

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void createUserTestFailureOnPasswordNotMatch() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USERNAME);
        createUserRequest.setPassword(TEST_PASSWORD);
        createUserRequest.setConfirmPassword("123");

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
    }
}
