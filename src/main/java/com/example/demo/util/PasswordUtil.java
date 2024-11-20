package com.example.demo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

public class PasswordUtil {
	
    // Secret key for AES encryption, should be kept secure
	 private static final String SECRET_KEY = "StrongSecretKey!";
	    /**
	     * Generates a password directly linked to the username.
	     * @param username The username to generate the password for.
	     * @return The generated encrypted password.
	     */
	    public static String generatePassword(String username) {
	    	try {
	            // Initialize AES encryption using the secret key
	            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
	            Cipher cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

	            // Encrypt the username
	            byte[] encryptedBytes = cipher.doFinal(username.getBytes());
	            
	            // Return the Base64 encoded encrypted password
	            return Base64.getEncoder().encodeToString(encryptedBytes);  // Ensure Base64 is valid
	        } catch (Exception e) {
	            throw new RuntimeException("Error generating password", e);
	        }
	    }

	    /**
	     * Decrypts the password to retrieve the username.
	     * @param password The encrypted password.
	     * @return The decrypted username.
	     */
	    public static String retrieveUsername(String password) {
	    	try {
	            // Clean up the password string before decoding
	            password = password.trim();  // Remove any extra whitespace that may cause issues with Base64 decoding
	            if (password.length() % 4 != 0) {
	                // Base64 strings need to be a multiple of 4 in length
	                password = password + "=".repeat(4 - password.length() % 4);  // Added padding
	            }

	            // Initialize AES decryption using the same secret key
	            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
	            Cipher cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.DECRYPT_MODE, keySpec);

	            // Decode the Base64 string and decrypt the bytes
	            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(password));  // Decode and decrypt
	            
	            // Return the decrypted username
	            return new String(decryptedBytes);  // Return the username
	        } catch (Exception e) {
	            throw new RuntimeException("Error retrieving username", e);
	        }
	    }
	    
	    
	    // Method to validate the password (verify it matches the username)
	    public static boolean validatePassword(String username, String password) {
	        return generatePassword(username).equals(password);
	    }
    
}



/**
 Algorithm Explanation:
Password Generation (AES Encryption):

1. We use AES encryption with the secret key to encrypt the username.
2. The encrypted data is then Base64 encoded to ensure it is in a string format suitable for storage or transmission.


Password Retrieval (AES Decryption):

1.When you need to retrieve the username, you take the Base64 encoded password, decode it, and then decrypt it using AES decryption.
2.The decrypted data is the original username.

Password Validation:

1. The validatePassword() method regenerates the password for the username using the generatePassword() method and compares it to the encrypted password.
2. If the generated password matches the provided password, then the username and password are valid.
 
 */
