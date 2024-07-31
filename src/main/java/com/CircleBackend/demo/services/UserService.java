package com.CircleBackend.demo.services;

import com.CircleBackend.demo.dto.UserReqDto;
import com.CircleBackend.demo.dto.UserResDto;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResDto createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User SaveUser=userRepository.save(user);
        UserResDto userResDto = new UserResDto();
        userResDto.setFirstName(SaveUser.getFirstName());
        userResDto.setLastName(SaveUser.getLastName());
        userResDto.setEmail(SaveUser.getEmail());
        userResDto.setPhone(SaveUser.getPhone());
        return userResDto;
    }

    public boolean authenticateUser(String Email, String password) {
        User admin = userRepository.findByEmail(Email)
                .orElseThrow(() -> new RuntimeException("User bulunamadı: " + Email));

        return passwordEncoder.matches(password, admin.getPassword());
    }
}
