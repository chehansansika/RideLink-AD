package com.ridelink.account.service;

import com.ridelink.account.dto.*;

public interface AccountService {

    AccountResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    AccountResponse getProfile(String email);

    AccountResponse updateProfile(String email, UpdateProfileRequest request);

    AccountResponse getAccountById(String id);
}
