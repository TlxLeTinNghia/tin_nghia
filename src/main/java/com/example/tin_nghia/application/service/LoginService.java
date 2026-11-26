package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.port.UserRepository;
import com.example.tin_nghia.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public Optional<User> execute(String userName , String password){
        if(userName == null || userName.isBlank()|| password == null || password.isEmpty()){
            return Optional.empty();
        }
//        Optional<User> foundUser = userRepository.findByUserName(userName);
//        if(foundUser.isEmpty()){
//            return Optional.empty();
//        }
//        User user = foundUser.get();
//        boolean passwordMatches = passwordEncoder.matches(password ,user.getPasswordHash());
//        if(!passwordMatches){
//            return Optional.empty();
//        }
//        return foundUser;
        return userRepository.findByUserName(userName.strip())
                .filter(user-> passwordEncoder.matches(password , user.getPasswordHash()));
        // Filter dùng để tìm xem sản phẩm có tồn tại hay không và kiểm tra nó có thõa điều kiện hay không ,
        // nếu không thì trả về Optional.empty()
        // nếu có thì trả về bình thường

        // => nếu không dùng filter thì viết dài như trên
    }
}
