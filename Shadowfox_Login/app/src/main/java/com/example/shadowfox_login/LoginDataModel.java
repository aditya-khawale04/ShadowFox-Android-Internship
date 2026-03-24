package com.example.shadowfox_login;

import java.security.PublicKey;

public class LoginDataModel {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String username;
    public String password;

    public LoginDataModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public LoginDataModel(){}

}
