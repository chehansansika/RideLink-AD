package com.ridelink.account.dto;

import com.ridelink.account.model.Role;

public class LoginResponse {

    private String token;
    private String accountId;
    private String email;
    private Role role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String accountId, String email, Role role) {
        this.token = token;
        this.accountId = accountId;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
