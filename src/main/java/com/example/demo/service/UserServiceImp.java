package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
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
	
	// ExecutorService for parallel processing
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
 // Initialize the logger for the class
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
	  @Override
	 // Register a new user (consider deleted users)
    public String registerUser(String username, String email, int age) {
        logger.info("Attempting to register user with username: {}", username);
        // Check if the username exists and is not deleted
        Optional<User> existingUser = UserRepo.findByUsernameAndIsDeletedFalse(username);
        if (existingUser.isPresent()) {
            logger.error("Username '{}' already exists and is active", username);
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if the username exists but is marked as deleted
        Optional<User> deletedUser = UserRepo.findByUsername(username);
        if (deletedUser.isPresent() && deletedUser.get().isDeleted()) {
            logger.error("Username '{}' was previously deleted and cannot be re-registered", username);
            throw new IllegalArgumentException("This username was previously deleted and cannot be re-registered");
        }

        // Generate the password using the username
        String generatedPassword = PasswordUtil.generatePassword(username);

     // Create a new user
        User newUser = new User(username, age, email);
        newUser.setDeleted(false); // Mark user as active

        // Save the user in the database
        UserRepo.save(newUser);

        logger.info("User '{}' registered successfully", username);
        return generatedPassword; // Return the generated password
    }

    // Find a user by username
    public Optional<User> findUserByUsername(String username) {
        logger.info("Searching for user with username: {}", username);
        Optional<User> user = UserRepo.findByUsername(username);
        if (user.isPresent()) {
            logger.info("User '{}' found", username);
        } else {
            logger.warn("User '{}' not found", username);
        }
        return user;
    }

    //Get all Users
    @Override
    public List<User> getAllUsers() {
    	logger.info("Fetching all users from the database");

        try {
            // Fetch all users from the database
            List<User> users = UserRepo.findAll();

            // Log the result count (optional)
            logger.info("Successfully fetched {} users from the database", users.size());

            return users;
        } catch (Exception e) {
            // Log any exceptions during the database call
            logger.error("Error occurred while fetching users from the database", e);
            throw new RuntimeException("Failed to fetch users", e);
        }
    }
    
    //get user by userId
    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);

        try {
            // Fetch the user by ID from the repository
            Optional<User> user = UserRepo.findById(id);

            // Log the outcome
            if (user.isPresent()) {
                logger.info("User with ID: {} found", id);
            } else {
                logger.warn("User with ID: {} not found", id);
            }

            return user;
        } catch (Exception e) {
            // Log any exceptions during the database call
            logger.error("Error occurred while fetching user with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch user by ID", e);
        }
    }
	
	

	// Bulk register users in parallel
    @Transactional
    public List<String> registerUsersInBulk(List<User> users) {
        logger.info("Starting bulk registration for {} users", users.size());

        // Create a list of Callable tasks for each user registration
        List<Callable<String>> tasks = users.stream()
            .map(user -> (Callable<String>) () -> registerSingleUser(user))
            .collect(Collectors.toList());

        try {
            // Submit all tasks to the executor and wait for results
            List<Future<String>> futures = executorService.invokeAll(tasks);

            // Collect results from futures
            return futures.stream()
                .map(future -> {
                    try {
                        return future.get(); // Retrieve the result of each task
                    } catch (ExecutionException | InterruptedException e) {
                        logger.error("Error during bulk registration", e);
                        return "Registration failed: " + e.getMessage();
                    }
                })
                .collect(Collectors.toList());
        } catch (InterruptedException e) {
            logger.error("Bulk registration interrupted", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Bulk registration interrupted", e);
        }
    }
    
 // Helper method to register a single user
    private String registerSingleUser(User user) {
        try {
            logger.info("Registering user with username: {}", user.getUsername());
            return registerUser(user.getUsername(), user.getEmail(), user.getAge());
        } catch (Exception e) {
            logger.error("Failed to register user with username: {}", user.getUsername(), e);
            return "Failed to register user: " + user.getUsername() + " - " + e.getMessage();
        }
    }
    
    // Check if the username already exists (both active and deleted users)
    private boolean isUsernameAlreadyRegistered(String username) {
        logger.info("Checking if username exists: {}", username);

        try {
            // Check if the username exists in the database
            Optional<User> existingUser = UserRepo.findByUsername(username);

            if (existingUser.isPresent()) {
                if (!existingUser.get().isDeleted()) {
                    // If user exists and is not deleted, log the info and return true
                    logger.info("User with username '{}' is already registered and active.", username);
                    return true;  // Username already exists and is not deleted
                } else {
                    // If user exists but is deleted, log the info and return false
                    logger.warn("User with username '{}' exists but is marked as deleted.", username);
                    return false;  // User is deleted, so allow re-registration
                }
            }

            // If user doesn't exist, log and return false
            logger.info("Username '{}' does not exist, proceeding with registration.", username);
            return false;  // User does not exist, so registration is allowed
        } catch (Exception e) {
            logger.error("Error occurred while checking if username '{}' exists", username, e);
            throw new RuntimeException("Error while checking username existence", e);
        }
    }
 
// Soft delete a user (mark as deleted)
    @Override
    @Transactional
    public void deleteUser(String username) {
        logger.info("Attempting to delete user with username: {}", username);
        Optional<User> user = UserRepo.findByUsernameAndIsDeletedFalse(username);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setDeleted(true);
            UserRepo.save(existingUser);
            logger.info("User '{}' deleted", username);
        } else {
            logger.warn("User '{}' not found or already deleted", username);
            throw new IllegalArgumentException("User not found or already deleted");
        }
    }

 //  Restore a deleted user (unmark as deleted)
    @Transactional
    public Optional<User> restoreUserByUsername(String username) {
        logger.info("Attempting to restore user with username: {}", username);

        try {
            // Fetch the user by username
            Optional<User> deletedUser = UserRepo.findByUsername(username);

            // Check if user exists and is deleted
            if (deletedUser.isPresent() && deletedUser.get().isDeleted()) {
                deletedUser.get().setDeleted(false);  // Restore the user (unmark as deleted)
                UserRepo.save(deletedUser.get()); // Save the restored user
                logger.info("User with username '{}' successfully restored", username);
                return deletedUser;
            } else {
                logger.warn("User with username '{}' not found or not deleted", username);
                throw new IllegalArgumentException("User not found or not deleted: " + username);
            }
        } catch (Exception e) {
            logger.error("Error occurred while restoring user with username: {}", username, e);
            throw new RuntimeException("Failed to restore user by username", e);
        }
    }
   
    
    public String generateEncryptedPassword(String username) {
        logger.info("Generating encrypted password for username: {}", username);

        if (username == null || username.isEmpty()) {
            logger.error("Invalid username provided.");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String encryptedPassword = PasswordUtil.generatePassword(username);
        logger.debug("Encrypted password for username '{}' is: {}", username, encryptedPassword);

        return encryptedPassword;
    }
    
    
    /**
     * Retrieves the username from the provided password.
     * @param password The encrypted password.
     * @return The username linked to the password.
     */
    public String retrieveUsernameFromPassword(String password) {
    	 // Log the incoming request at INFO level
        logger.info("Attempting to retrieve username for the provided password.");
        
        try {
            // Call the method to retrieve the username
            String username = PasswordUtil.retrieveUsername(password);
            
            // Log the result at INFO level (success)
            logger.info("Successfully retrieved username: {}", username);
            return username;
        } catch (Exception e) {
            // Log the error at ERROR level
            logger.error("Error occurred while retrieving username for the provided password.", e);
            throw new RuntimeException("Error occurred while retrieving username.", e); // Re-throw the exception or handle accordingly
        
    }  
        
    }

       
}
