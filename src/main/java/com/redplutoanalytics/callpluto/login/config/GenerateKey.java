package com.redplutoanalytics.callpluto.login.config;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class GenerateKey {
	
	
	 public static void main(String[] args) {
	        // Generate secure random secret key for HS512
	        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	        
	        // Encode key to Base64 so it can be stored in application.properties
	        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
	        
	        System.out.println("Generated Secret Key (Base64):");
	        System.out.println(base64Key);
	    }
	}
