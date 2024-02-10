package com.Jason.DevInternHubBackend.service;

import java.util.Optional;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;

// The Spring framework needs an implementation of UserDetailService so it knows how to retrieve user details.
// The UserDetailsServiceImpl service is meant to be passed to the `AuthenticationManagerBuilder` bean
// as part of the global configuration.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final AppUserRepository appUserRepository;

	public UserDetailsServiceImpl(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AppUser> user = appUserRepository.findByUsernameIgnoreCase(username);
		UserBuilder builder = null;
		if (user.isPresent()) {
			AppUser currentUser = user.get();
			builder = org.springframework.security.core.userdetails.User.withUsername(username);
			builder.password(currentUser.getPassword());
			builder.roles(currentUser.getRole().toString());
		} else {
			throw new UsernameNotFoundException("User not found.");
		}
		return builder.build();
	}
}
