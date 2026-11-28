package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.exception.UserNotFoundException;
import com.example.tin_nghia.application.port.UserRepository;
import com.example.tin_nghia.domain.User;
import org.springframework.stereotype.Service;

@Service
public class GetUserService {
    private final UserRepository userRepository;
    public GetUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User execute(int userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new UserNotFoundException(userId));
    }
}
