package com.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.model.Usertable;
import com.security.repo.RepoClass;

@Service
public class UserService 
{
	@Autowired
	private RepoClass repo;
	
	@Autowired
	private JwtHelper jwtHelper;
	
	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
    private MyUserDetailsService userDetailsService;
	
//	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	public Usertable register(Usertable user)
	{
//		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}

	public String verify(Usertable user) 
	{
		
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));               
		
//		if(authentication.isAuthenticated())
//		{
//			return service.generateToken(user.getUsername());
//		}
//		return "fail"; 	
		
		 if (authentication.isAuthenticated())
	        {
	            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

	            String token = jwtHelper.generateToken(userDetails); // Generate JWT token
	            
	            // Fetch user from the database and update token
	            Usertable dbUser = repo.findByUsername(user.getUsername());
	            if (dbUser != null) 
	            {
	                dbUser.setToken(token); // Set the token in the user object
	                repo.save(dbUser);      // Save the updated user to the database
	            }
	            
	            return token;
	        }
		 return "fail";		
	}
}



