package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

	@Configuration
	@EnableWebSecurity
	public class SecurityConfig {

		@Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable()
	            .authorizeRequests()
	            .requestMatchers(HttpMethod.GET, "/api/users/all").permitAll()
	            .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()   
	            .requestMatchers(HttpMethod.PUT, "/api/users/update/**").permitAll()   
	            .requestMatchers(HttpMethod.DELETE, "/api/users/delete/**").permitAll() 
	            .requestMatchers(HttpMethod.POST,"/api/users/register/bulk").permitAll()	        
	            .requestMatchers(HttpMethod.GET,"/api/users/username/from-password").permitAll();
	            
	        return http.build();
	    }
	}


