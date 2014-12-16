package com.potlatch.server.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.potlatch.server.repository.UserInfo;
import com.potlatch.server.repository.UserInfoRepository;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired(required = true)		
	private UserInfoRepository ui_repro;	
	
	public CustomUserDetailsService()
	{
		super();

		if (ui_repro != null && ui_repro.count() <= 0)
		{
			this.ui_repro.save(new UserInfo("admin", "pass"));
		}

	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User udetail = null;
		
		if (ui_repro != null)
		{		
			UserInfo user = (UserInfo) ui_repro.findByUsername(username);
			udetail = (User) User.create(
					user.getUsername(), 
					user.getPassword(), "ADMIN", "USER");		
		}
		return udetail;
	}

}
