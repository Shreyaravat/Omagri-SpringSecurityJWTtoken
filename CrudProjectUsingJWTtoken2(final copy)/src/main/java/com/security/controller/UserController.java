package com.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.model.Usertable;
import com.security.service.UserService;

@RestController
public class UserController 
{
	@Autowired
	private UserService service;
	
	@PostMapping("/register")
	public Usertable register(@RequestBody Usertable user)
	{
		return service.register(user);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody Usertable user)
	{
		return service.verify(user);
	}	
}







