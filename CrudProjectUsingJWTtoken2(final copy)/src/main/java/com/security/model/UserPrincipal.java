package com.security.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails 
{

	private Usertable user;
	
	public UserPrincipal(Usertable user)
	{
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return Collections.singleton(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public String getPassword() 
	{
		return user.getPassword();
	}

	@Override
	public String getUsername() 
	{
		return user.getUsername();
	}

	public  boolean isAccountNonExpired() 
	{
		return true;
	}

	
	public boolean isAccountNonLocked()
	{
		return true;
	}

	public boolean isCredentialsNonExpired() 
	{
		return true;
	}

	public boolean isEnabled()
	{
		return true;
	}	
}





//Purpose: Represents a custom implementation of UserDetails for Spring Security.
