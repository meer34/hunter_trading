package com.hunter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hunter.web.model.Role;
import com.hunter.web.model.User;

@SuppressWarnings("serial")
public class MyUserDetails implements UserDetails {
	
	private User user;

	public MyUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPin();
	}

	@Override
	public String getUsername() {
		return user.getPhone();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		if(user.isOtpNonExpired(30L)) {
			System.out.println("Entered OTP for " + user.getPhone() + " has expired!");
			return true;
			
		} else return false;
	}

	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}
	
	public User getUser() {
		return user;
	}

}
