package com.CircleBackend.demo.controller;

import com.CircleBackend.demo.dto.UserResDto;
import com.CircleBackend.demo.dto.UserWalletResDto;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserResDto> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }
    @GetMapping("/getuser/{id}")
    public ResponseEntity<UserWalletResDto> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id),HttpStatus.FOUND);
    }
}
