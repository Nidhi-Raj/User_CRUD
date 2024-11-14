package com.example.demo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

public class PasswordUtil {
	
	// A static salt for basic hashing (you can improve this in real-world applications)
    private static final String SALT = "secret_salt_for_encryption"; // Should be more complex and unique in production

    public static String encryptPassword(String username) {
        try {
            // Combine username with salt to create a stronger hash
            String combined = username + SALT;

            // Create a MessageDigest instance with SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing
            byte[] hash = digest.digest(combined.getBytes());

            // Return the hashed password as a Base64 encoded string
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encryption error: " + e.getMessage());
        }
    }
    
    
 // Method to validate if a raw username matches the hashed password
    public static boolean validatePassword(String username, String hashedPassword) {
        String generatedHash = encryptPassword(username);
        return generatedHash.equals(hashedPassword);
    }

    // Method to retrieve username from hashed password (by comparing with the database)
    public static String getUserNameFromEncryptedPassword(String encryptedPassword, UserRepository UserRepo) {
        // Iterate through all users in the database to find a matching hashed password
        for (User user : UserRepo.findAll()) {
            if (user.getPassword().equals(encryptedPassword)) {
                return user.getUsername();  // If the hashes match, return the username
            }
        }
        throw new IllegalArgumentException("No user found for the given encrypted password");
    }
    
    

  
    
}
