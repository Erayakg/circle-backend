package com.CircleBackend.demo.controller;


import com.CircleBackend.demo.dto.LoginForm;
import com.CircleBackend.demo.services.UserService;
import com.CircleBackend.demo.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin()
@RestController
@RequestMapping("/v1/auth")
public class LoginController {
    @Autowired
    private final UserService userService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private AuthenticationManager manager;

    private static final String SECRET_KEY = "UGoVm42Ti5jbIPXTMTHLYAZILANIYASAMAYADEWAMADAMOLANACOKBILE"; // Güçlü bir secret key

    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public String authenticatedAndGenerate(@RequestBody LoginForm authRequest){
        Authentication authenticate = manager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()){
            return jwtService.generateToken(authRequest.getUsername());
        }
        else {
            throw  new UsernameNotFoundException("invalid user");
        }
    }
}