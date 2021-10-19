package com.projects.bookhere.controller;

import com.projects.bookhere.model.User;
import com.projects.bookhere.model.UserRole;
import com.projects.bookhere.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/* Handle user register requests */
@RestController
public class RegisterController {
    private RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    //Register a guest user
    @PostMapping("/register/guest")
    public void addGuest(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_GUEST);
    }

    //Register a host user
    @PostMapping("/register/host")
    public void addHost(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_HOST);
    }
}