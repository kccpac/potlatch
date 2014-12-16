package com.potlatch.server.repository;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;

@Entity
public class UserInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
//	private Collection<GrantedAuthority> authorities_;
	
	private String username;
	private String password;
	
	UserInfo()
	{
	}
	
	public UserInfo(String username, String password)
	{
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public long getId()
	{
		return id;
	}
	

}
