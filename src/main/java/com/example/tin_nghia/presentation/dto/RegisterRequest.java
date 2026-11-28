package com.example.tin_nghia.presentation.dto;

public class RegisterRequest {
    private String userName;
    private String password;
    public RegisterRequest(){}
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
