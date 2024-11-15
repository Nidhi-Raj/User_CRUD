package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	// Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username) {
        try {
            String password=userService.registerUser(username);
            return ResponseEntity.ok("User registered successfully. Password: " + password);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
	 // Endpoint to bulk register users
    @PostMapping("/register/bulk")
    public ResponseEntity<String> bulkRegister(@RequestBody List<String> usernames) {
        try {
            userService.bulkRegisterUsers(usernames);
            return ResponseEntity.ok("Users registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering users: " + e.getMessage());
        }
    }
	
	//Retrive all the users
	@GetMapping("/all")
	public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
	
	// Retrieve a user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
 // GET request to fetch a user by their username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String username) {
        Optional<User> user = userService.getUserByUserName(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the user is not found
        }
    }
    
 // Delete a user by username (soft delete)
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        try {
            userService.deleteUserByUserName(username);
            return ResponseEntity.ok("User deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
 // Restore a deleted user
    @PutMapping("/restore/{username}")
    public ResponseEntity<String> restoreUserByUsername(@PathVariable String username) {
        try {
            userService.restoreUserByUsername(username);
            return ResponseEntity.ok("User restored successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    
 // Endpoint to get the username from an encrypted password (hash)
    @GetMapping("/username/from-encrypted-password")
    public ResponseEntity<String> getUserNameFromEncryptedPassword(@RequestParam String encryptedPassword) {
        try {
            // Fetch the username associated with the encrypted password
            String username = userService.getUserNameFromEncryptedPassword(encryptedPassword);
            return ResponseEntity.ok("Username: " + username); // Return the username
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // Return error if no user found
        }
    }

}
