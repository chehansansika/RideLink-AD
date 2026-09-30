package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.exception.*;
import com.ridelink.account.model.Account;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.repository.AccountRepository;
import com.ridelink.account.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AccountServiceImpl(AccountRepository accountRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AccountResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("Email is already registered: " + request.getEmail());
        }

        LocalDateTime now = LocalDateTime.now();
        Account account = new Account(
                null,
                request.getFirstName().trim(),
                request.getLastName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                request.getPhone().trim(),
                request.getRole(),
                AccountStatus.ACTIVE,
                now,
                now
        );

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(savedAccount);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        Account account = accountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw new AccountSuspendedException("Account has been suspended. Please contact support.");
        }

        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new AccountInactiveException("Account is inactive. Please activate your account.");
        }

        String token = jwtService.generateToken(account.getEmail(), account.getRole().name(), account.getId());

        return new LoginResponse(token, account.getId(), account.getEmail(), account.getRole());
    }

    @Override
    public AccountResponse getProfile(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        Account account = accountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with email: " + email));

        return AccountResponse.fromEntity(account);
    }

    @Override
    public AccountResponse updateProfile(String email, UpdateProfileRequest request) {
        String normalizedEmail = email.toLowerCase().trim();
        Account account = accountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with email: " + email));

        account.setFirstName(request.getFirstName().trim());
        account.setLastName(request.getLastName().trim());
        account.setPhone(request.getPhone().trim());
        account.setUpdatedAt(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }

    @Override
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        return AccountResponse.fromEntity(account);
    }
}
