package com.redplutoanalytics.callpluto.login.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.redplutoanalytics.callpluto.model.Users;
import com.redplutoanalytics.callpluto.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	 @Autowired
	    private JwtUtils jwtUtils;

	    @Autowired
	    private UserRepository userRepository;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {
	        String jwt = parseJwt(request);
	        if (jwt != null && jwtUtils.validateToken(jwt)) {
	            String username = jwtUtils.getUsernameFromToken(jwt);
	            Users user = userRepository.findByUsername(username).orElse(null);
	            if (user != null) {
	                UsernamePasswordAuthenticationToken authentication =
	                        new UsernamePasswordAuthenticationToken(user, null, null);
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }
	        filterChain.doFilter(request, response);
	    }

	    private String parseJwt(HttpServletRequest request) {
	        String headerAuth = request.getHeader("Authorization");
	        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
	            return headerAuth.substring(7);
	        }
	        return null;
	    }
	}