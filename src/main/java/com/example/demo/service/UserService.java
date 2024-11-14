package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;

public interface UserService {

public List<User> getAllUsers();

public Optional<User> getUserByUserName(String username);

public Optional<User> getUserById(Long id);

public void bulkRegisterUsers(List<String> userNames);

public String registerUser(String username);

public Optional<User> deleteUserByUserName(String username);

public Optional<User> restoreUserByUsername(String username);

public String getUserNameFromEncryptedPassword(String encryptedPassword) ;

}
