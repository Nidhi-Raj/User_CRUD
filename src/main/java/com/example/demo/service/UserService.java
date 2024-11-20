package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;

public interface UserService {
	
public String registerUser(String username, String email, int age);

public List<String> registerUsersInBulk(List<User> users);


public Optional<User> findUserByUsername(String username);

public List<User> getAllUsers();

public Optional<User> getUserById(Long id);

public void deleteUser(String username);

public Optional<User> restoreUserByUsername(String username);

public String generateEncryptedPassword(String username);

public String retrieveUsernameFromPassword(String password);



//public String getUserNameFromEncryptedPassword(String encryptedPassword) ;


}
