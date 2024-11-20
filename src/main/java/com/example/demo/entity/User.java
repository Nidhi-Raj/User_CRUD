package com.example.demo.entity;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	
    @Column
    @NotNull
    private String username; 
    
    private boolean isDeleted; 
    
    @Column
    private int age;
    
    @Column
    private String email;
    
    @Column
    private String password;

    public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	// Default constructor
    public User() {}
     
    
    public User(String username, int age, String email) {
		super();
		this.username = username;
		this.age = age;
		this.email = email;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public boolean isDeleted() {
		return isDeleted;
	}


	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
