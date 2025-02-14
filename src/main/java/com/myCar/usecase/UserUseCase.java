package com.myCar.usecase;

import java.util.List;

import com.myCar.domain.User;

public interface UserUseCase {
    User getUserById(Long id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
	List<User> getAllUsers();
	User authenticateUser(String email, String password);
}
