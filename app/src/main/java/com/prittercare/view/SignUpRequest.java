package com.prittercare.view;

public class SignUpRequest {
    private String username;
    private String password;

    public SignUpRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}