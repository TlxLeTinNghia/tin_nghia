package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.exception.UserNameAlreadyExistsException;
import com.example.tin_nghia.application.port.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public CreateUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public void execute(String userName , String password){
        // Check tên đăng nhập
        String normalizeUserName = userName.strip();
        if(userRepository.findByUserName(normalizeUserName).isPresent()){
            throw new UserNameAlreadyExistsException();
        }
        String passwordHash = passwordEncoder.encode(password);
        // Lưu tài khoản
        userRepository.insert(normalizeUserName, passwordHash);
    }
}
