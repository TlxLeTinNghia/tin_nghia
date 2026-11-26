package com.example.tin_nghia.domain;

public class User {
    private final int userId;
    private final String userName;
    private final String passwordHash;
    private final Role role;

    public User(int userId , String userName , String passwordHash , Role role){
        this.userId = userId;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(int userId){
        this.userId = userId;
        this.userName = "";
        this.passwordHash = "";
        this.role = Role.USER;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}
