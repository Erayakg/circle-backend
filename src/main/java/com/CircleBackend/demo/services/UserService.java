package com.CircleBackend.demo.services;

import com.CircleBackend.demo.dto.UserResDto;
import com.CircleBackend.demo.dto.UserWalletResDto;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.mapper.UserMapper;
import com.CircleBackend.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

    public UserWalletResDto getUser(Long id) {
        Optional<User> byId = userRepository.findById(id);
        UserWalletResDto dto = userMapper.toDto(byId.get());

        return dto;
    }
}
