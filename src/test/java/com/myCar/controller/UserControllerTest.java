package com.myCar.controller;

import com.myCar.domain.User;
import com.myCar.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User("testuser", "test@example.com", "password");
    }

    @Test
    public void testGetUserById_Success() {
        when(userService.getUserById(anyLong())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByUsername_Success() {
        when(userService.getUserByUsername(anyString())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        when(userService.getUserByUsername(anyString())).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    public void testGetUserByEmail_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    public void testGetUserByEmail_NotFound() {
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    public void testCreateUser_Success() {
        when(userService.saveUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    public void testSignInUser_Success() {
        when(userService.authenticateUser(anyString(), anyString())).thenReturn(user);

        UserController.SignInRequest signInRequest = new UserController.SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        ResponseEntity<User> response = userController.signInUser(signInRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).authenticateUser("test@example.com", "password");
    }

    @Test
    public void testSignInUser_Failure() {
        when(userService.authenticateUser(anyString(), anyString())).thenReturn(null);

        UserController.SignInRequest signInRequest = new UserController.SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        ResponseEntity<User> response = userController.signInUser(signInRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).authenticateUser("test@example.com", "password");
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(anyLong());

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }
}
