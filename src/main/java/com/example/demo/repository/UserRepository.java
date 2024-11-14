package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	 // Case-insensitive search for the username
	Optional<User> findByUsername(String username);
    
	// Find a user by their username (only those who are not deleted)
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
	
	Optional<User> deleteByUsername(String username);
	
	Optional<User> findByPassword(String password);

	
	
	List<User> findAll(); // Fetch all users
}
