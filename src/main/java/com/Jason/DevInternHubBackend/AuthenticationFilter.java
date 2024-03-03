package com.Jason.DevInternHubBackend;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Jason.DevInternHubBackend.service.JwtService;
import com.Jason.DevInternHubBackend.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsServiceImpl;

	public AuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
		super();
		this.jwtService = jwtService;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, java.io.IOException {
		// Get token from the Authorization header
		String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (jws != null) {
			// Verify token and get user
			String username = jwtService.getAuthUser(request);
			response.setHeader("X-User-Username", username);
			UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
			// Authenticate
			Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}
}