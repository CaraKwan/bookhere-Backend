package com.projects.bookhere.service;

import com.projects.bookhere.exception.UserNotExistException;
import com.projects.bookhere.model.Authority;
import com.projects.bookhere.model.Token;
import com.projects.bookhere.model.User;
import com.projects.bookhere.model.UserRole;
import com.projects.bookhere.repository.AuthorityRepository;
import com.projects.bookhere.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/* Handel user login, if succeed, return token */
@Service
public class AuthenticationService {
    private AuthenticationManager authenticationManager;
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    public Token authenticate(User user, UserRole role) throws UserNotExistException {
        //Check user credential
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (AuthenticationException exception) {
            throw new UserNotExistException("User Doesn't Exist");
        }

        //Check user role
        Authority authority = authorityRepository.findById(user.getUsername()).orElse(null);
        if (!authority.getAuthority().equals(role.name())) {
            throw new UserNotExistException("User Doesn't Exist");
        }

        //Return a token for authenticated user
        return new Token(jwtUtil.generateToken(user.getUsername()));
    }
}