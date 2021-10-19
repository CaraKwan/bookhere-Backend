package com.projects.bookhere.service;

import com.projects.bookhere.exception.UserAlreadyExistException;
import com.projects.bookhere.model.Authority;
import com.projects.bookhere.model.User;
import com.projects.bookhere.model.UserRole;
import com.projects.bookhere.repository.AuthorityRepository;
import com.projects.bookhere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/* Handel user register, if succeed, save data to database */
@Service
public class RegisterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterService(UserRepository userRepository,
                           AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Atomicity, Each process waits in queue and is done one by one
    @Transactional(isolation = Isolation.SERIALIZABLE)
    //Save user and its authority to database
    public void add(User user, UserRole role) throws UserAlreadyExistException {
        if (userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));  //Encode password
        user.setEnabled(true);

        //Save user and authority to database
        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));
    }
}