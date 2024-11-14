package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.PasswordUtil;


import jakarta.transaction.Transactional;


@Service
@Transactional
public class UserServiceImp implements UserService{

	@Autowired
	private UserRepository UserRepo;
	
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    
    // Register a user (check if user is deleted)
    public String registerUser(String username) {
        // Check if the username exists and is marked as deleted
        Optional<User> existingUser = UserRepo.findByUsernameAndIsDeletedFalse(username);
        
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if the username exists but is deleted
        Optional<User> deletedUser = UserRepo.findByUsername(username);
        if (deletedUser.isPresent() && deletedUser.get().isDeleted()) {
            throw new IllegalArgumentException("This username was previously deleted and cannot be re-registered");
        }

        // Proceed to register the new user
        String password = PasswordUtil.encryptPassword(username);  // Use your password encryption logic
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDeleted(false); // Set user as not deleted
       UserRepo.save(user);
       return password;
         }
    
    // method to find a user by username
    public Optional<User> findUserByUsername(String username) {
        System.out.println("Searching for user with username: " + username);
        return UserRepo.findByUsername(username);  // Directly call the repository
    }


	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
	     return UserRepo.findAll();
	    }
	@Override
	 public Optional<User> getUserById(Long id) {
	        return UserRepo.findById(id);
	    }
	
	@Override
	public Optional<User> getUserByUserName(String username) {
		// TODO Auto-generated method stub
		return UserRepo.findByUsername(username);
		}
	
	

	// Bulk register users in parallel
    @Transactional
    public void bulkRegisterUsers(List<String> usernames) {
        usernames.parallelStream().forEach(username -> {
            try {
                // Before registering the user, check if the username is already taken or deleted
                if (isUsernameAlreadyRegistered(username)) {
                    System.out.println("User with username " + username + " already exists.");
                    return;  // Skip user registration if username already exists
                }

                // Proceed to register the new user if username is not already registered
                registerUser(username);
            } catch (Exception e) {
                System.out.println("Error registering user: " + username + ". Error: " + e.getMessage());
            }
        });
    }
    
    
    // Check if the username already exists (both active and deleted users)
    private boolean isUsernameAlreadyRegistered(String username) {
        System.out.println("Checking if username exists: " + username);
        Optional<User> existingUser = UserRepo.findByUsername(username);

        if (existingUser.isPresent()) {
            if (!existingUser.get().isDeleted()) {
                System.out.println("User with username " + username + " is already registered and active.");
                return true;  // Username already exists and is not deleted
            } else {
                System.out.println("User with username " + username + " exists but is marked as deleted.");
                return false;  // User is deleted, so allow re-registration
            }
        }

        System.out.println("Username " + username + " does not exist, proceeding with registration.");
        return false;  // User does not exist, so registration is allowed
    }
 
// Soft delete a user (mark as deleted)
    @Override
    @Transactional
    public Optional<User> deleteUserByUserName(String username) {
        Optional<User> user = UserRepo.findByUsernameAndIsDeletedFalse(username);

        if (user.isPresent()) {
            // Soft delete (mark as deleted)
            user.get().setDeleted(true);
            UserRepo.save(user.get());
            return user;
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

 //  Restore a deleted user (unmark as deleted)
    @Transactional
    public Optional<User> restoreUserByUsername(String username) {
        Optional<User> deletedUser = UserRepo.findByUsername(username);
        if (deletedUser.isPresent() && deletedUser.get().isDeleted()) {
            deletedUser.get().setDeleted(false);  // Restore the user (unmark as deleted)
            UserRepo.save(deletedUser.get());
            return deletedUser;
        } else {
            throw new IllegalArgumentException("User not found or not deleted: " + username);
        }
    }
    
    @Override
 // Method to retrieve the username based on an encrypted password (hash)
    public String getUserNameFromEncryptedPassword(String encryptedPassword) {
        return PasswordUtil.getUserNameFromEncryptedPassword(encryptedPassword, UserRepo);
    }

    
    
}
	
	

