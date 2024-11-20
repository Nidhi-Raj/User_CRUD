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
    public String registerUser(@RequestParam String username,
            @RequestParam String email,
            @RequestParam int age) {
		return userService.registerUser(username, email, age);
		}
    
 // Bulk registration endpoint
    @PostMapping("/register/bulk")
    public List<String> registerUsersInBulk(@RequestBody List<User> users) {
        return userService.registerUsersInBulk(users);
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
 // Find a user by username
    @GetMapping("/find")
    public Optional<User> findUser(@RequestParam String username) {
        return userService.findUserByUsername(username);
    }
    
    // Delete a user by username
    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return "User deleted successfully.";
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
    
    //Endpoint to get password from username.
    @GetMapping("/{username}/password")
    public String getEncryptedPassword(@PathVariable String username) {
        return userService.generateEncryptedPassword(username);
    }
    
    /**
     * Endpoint to retrieve the username linked to a password.
     * @param password The encrypted password.
     * @return The username linked to the password.
     */
    @GetMapping("/retrieve-username")
    public String retrieveUsername(@RequestParam String password) {
        return userService.retrieveUsernameFromPassword(password);
    }

}
