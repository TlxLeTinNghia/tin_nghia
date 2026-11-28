package com.example.tin_nghia.application.port;

import com.example.tin_nghia.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUserName(String userName);
    void insert(String userName , String passwordHash);
}
