package com.Jason.DevInternHubBackend;

import java.io.IOException;
import java.security.Key;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Jason.DevInternHubBackend.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final Key key = JwtService.key;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (token != null && token.startsWith(JwtService.PREFIX)) {
			token = token.substring(7);
			try {
				Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build()
						.parseClaimsJws(token.replace(JwtService.PREFIX, ""));
				String role = claims.getBody().get("role", String.class);
				response.setHeader("X-User-Role", role);
			} catch (Exception e) {
				// invalid token
				response.setHeader("X-User-Role", "ROLE_GUEST");
			}
		} else {
			response.setHeader("X-User-Role", "ROLE_GUEST");
		}
		filterChain.doFilter(request, response);

	}
}
