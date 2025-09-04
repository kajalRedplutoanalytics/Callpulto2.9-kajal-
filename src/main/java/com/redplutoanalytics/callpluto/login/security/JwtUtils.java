package com.redplutoanalytics.callpluto.login.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

	 @Value("${jwt.secret}")
	    private String jwtSecret;

	    @Value("${jwt.expiration}")
	    private int jwtExpirationMs;

	    private SecretKey secretKey;

	    @PostConstruct
	    public void init() {
	        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
	        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
	    }

	    public String generateToken(String username) {
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
	                .signWith(secretKey, SignatureAlgorithm.HS512)
	                .compact();
	    }

	    public boolean validateToken(String token) {
	        try {
	            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
	            return true;
	        } catch (JwtException | IllegalArgumentException e) {
	            return false;
	        }
	    }

	    public String getUsernameFromToken(String token) {
	        return Jwts.parserBuilder().setSigningKey(secretKey).build()
	                .parseClaimsJws(token).getBody().getSubject();
	    }
	}
