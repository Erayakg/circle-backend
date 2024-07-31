package com.CircleBackend.demo.services;

import com.CircleBackend.demo.config.UserDetail;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo = userRepository.findByEmail(email);
        return userInfo.map(UserDetail::new).
                orElseThrow(() -> new UsernameNotFoundException("user not found" + email));
    }
}