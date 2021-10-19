package com.projects.bookhere.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

/*Generate and parse token */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")  //Map to jwt.secret in application.properties
    private String secret;   //Private key

    //Generate a token
    public String generateToken(String subject) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    //Get the payload of token
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //Get username from payload
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    //Get expiration time from payload
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    //Validate if the expiration time is still valid or not
    public Boolean validateToken(String token) {
        return extractExpiration(token).after(new Date());
    }
}