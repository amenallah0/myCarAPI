package com.myCar.service;

import com.myCar.domain.User;
import com.myCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = new User("username", "email@example.com", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User retrievedUser = userService.getUserById(userId);

        assertEquals(user, retrievedUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserByUsername() {
        String username = "username";
        User user = new User("username", "email@example.com", "password");

        when(userRepository.findByUsername(username)).thenReturn(user);

        User retrievedUser = userService.getUserByUsername(username);

        assertEquals(user, retrievedUser);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "email@example.com";
        User user = new User("username", "email@example.com", "password");

        when(userRepository.findByEmail(email)).thenReturn(user);

        User retrievedUser = userService.getUserByEmail(email);

        assertEquals(user, retrievedUser);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testSaveUser() {
        User user = new User("username", "email@example.com", "password");
        User savedUser = new User("username", "email@example.com", "password");

        when(userRepository.save(user)).thenReturn(savedUser);

        User returnedUser = userService.saveUser(user);

        assertEquals(savedUser, returnedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
