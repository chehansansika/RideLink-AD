package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.exception.*;
import com.ridelink.account.model.Account;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.repository.AccountRepository;
import com.ridelink.account.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account sampleAccount;
    private RegisterRequest sampleRegisterRequest;
    private LoginRequest sampleLoginRequest;

    @BeforeEach
    void setUp() {
        sampleAccount = new Account(
                "acc-101",
                "John",
                "Perera",
                "john@example.com",
                "$2a$10$encodedPasswordHash",
                "0771234567",
                Role.PASSENGER,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleRegisterRequest = new RegisterRequest(
                "John",
                "Perera",
                "john@example.com",
                "Password123",
                "0771234567",
                Role.PASSENGER
        );

        sampleLoginRequest = new LoginRequest(
                "john@example.com",
                "Password123"
        );
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register a new account")
        void testRegister_Success() {
            when(accountRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Password123")).thenReturn("$2a$10$encodedPasswordHash");
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
                Account saved = invocation.getArgument(0);
                saved.setId("acc-101");
                return saved;
            });

            AccountResponse response = accountService.register(sampleRegisterRequest);

            assertNotNull(response);
            assertEquals("acc-101", response.getId());
            assertEquals("John", response.getFirstName());
            assertEquals("Perera", response.getLastName());
            assertEquals("john@example.com", response.getEmail());
            assertEquals("0771234567", response.getPhone());
            assertEquals(Role.PASSENGER, response.getRole());
            assertEquals(AccountStatus.ACTIVE, response.getStatus());

            verify(accountRepository, times(1)).existsByEmail("john@example.com");
            verify(accountRepository, times(1)).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists")
        void testRegister_DuplicateEmail() {
            when(accountRepository.existsByEmail("john@example.com")).thenReturn(true);

            DuplicateEmailException exception = assertThrows(
                    DuplicateEmailException.class,
                    () -> accountService.register(sampleRegisterRequest)
            );

            assertTrue(exception.getMessage().contains("already registered"));
            verify(accountRepository, times(1)).existsByEmail("john@example.com");
            verify(accountRepository, never()).save(any(Account.class));
        }

        @Test
        @DisplayName("Should verify password hashing during registration")
        void testRegister_PasswordHashing() {
            when(accountRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Password123")).thenReturn("$2a$10$hashedSecurely");
            when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

            accountService.register(sampleRegisterRequest);

            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
            verify(accountRepository).save(accountCaptor.capture());
            Account saved = accountCaptor.getValue();

            assertEquals("$2a$10$hashedSecurely", saved.getPassword());
            assertNotEquals("Password123", saved.getPassword());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully authenticate and return JWT token")
        void testLogin_Success() {
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));
            when(passwordEncoder.matches("Password123", sampleAccount.getPassword())).thenReturn(true);
            when(jwtService.generateToken("john@example.com", "PASSENGER", "acc-101")).thenReturn("mock-jwt-token");

            LoginResponse response = accountService.login(sampleLoginRequest);

            assertNotNull(response);
            assertEquals("mock-jwt-token", response.getToken());
            assertEquals("acc-101", response.getAccountId());
            assertEquals("john@example.com", response.getEmail());
            assertEquals(Role.PASSENGER, response.getRole());

            verify(accountRepository, times(1)).findByEmail("john@example.com");
            verify(passwordEncoder, times(1)).matches("Password123", sampleAccount.getPassword());
            verify(jwtService, times(1)).generateToken("john@example.com", "PASSENGER", "acc-101");
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when email is not found")
        void testLogin_UnknownEmail() {
            when(accountRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            LoginRequest unknownUserRequest = new LoginRequest("unknown@example.com", "Password123");

            InvalidCredentialsException exception = assertThrows(
                    InvalidCredentialsException.class,
                    () -> accountService.login(unknownUserRequest)
            );

            assertEquals("Invalid email or password", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password does not match")
        void testLogin_WrongPassword() {
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));
            when(passwordEncoder.matches("WrongPassword", sampleAccount.getPassword())).thenReturn(false);

            LoginRequest wrongPassRequest = new LoginRequest("john@example.com", "WrongPassword");

            InvalidCredentialsException exception = assertThrows(
                    InvalidCredentialsException.class,
                    () -> accountService.login(wrongPassRequest)
            );

            assertEquals("Invalid email or password", exception.getMessage());
            verify(jwtService, never()).generateToken(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw AccountSuspendedException when account is suspended")
        void testLogin_SuspendedAccount() {
            sampleAccount.setStatus(AccountStatus.SUSPENDED);
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));
            when(passwordEncoder.matches("Password123", sampleAccount.getPassword())).thenReturn(true);

            AccountSuspendedException exception = assertThrows(
                    AccountSuspendedException.class,
                    () -> accountService.login(sampleLoginRequest)
            );

            assertTrue(exception.getMessage().contains("suspended"));
            verify(jwtService, never()).generateToken(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when account is inactive")
        void testLogin_InactiveAccount() {
            sampleAccount.setStatus(AccountStatus.INACTIVE);
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));
            when(passwordEncoder.matches("Password123", sampleAccount.getPassword())).thenReturn(true);

            AccountInactiveException exception = assertThrows(
                    AccountInactiveException.class,
                    () -> accountService.login(sampleLoginRequest)
            );

            assertTrue(exception.getMessage().contains("inactive"));
            verify(jwtService, never()).generateToken(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Profile Tests")
    class ProfileTests {

        @Test
        @DisplayName("Should successfully retrieve user profile by email")
        void testGetProfile_Success() {
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));

            AccountResponse response = accountService.getProfile("john@example.com");

            assertNotNull(response);
            assertEquals("acc-101", response.getId());
            assertEquals("John", response.getFirstName());
            assertEquals("Perera", response.getLastName());
            assertEquals("john@example.com", response.getEmail());
            assertEquals(Role.PASSENGER, response.getRole());
            assertEquals(AccountStatus.ACTIVE, response.getStatus());
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when profile email does not exist")
        void testGetProfile_NotFound() {
            when(accountRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            assertThrows(
                    AccountNotFoundException.class,
                    () -> accountService.getProfile("nonexistent@example.com")
            );
        }

        @Test
        @DisplayName("Should successfully update profile and preserve role, status, password")
        void testUpdateProfile_Success() {
            when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleAccount));
            when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

            UpdateProfileRequest updateRequest = new UpdateProfileRequest("Johnny", "Silva", "0779998888");

            AccountResponse updated = accountService.updateProfile("john@example.com", updateRequest);

            assertNotNull(updated);
            assertEquals("Johnny", updated.getFirstName());
            assertEquals("Silva", updated.getLastName());
            assertEquals("0779998888", updated.getPhone());
            // Ensure security fields are unaffected
            assertEquals(Role.PASSENGER, updated.getRole());
            assertEquals(AccountStatus.ACTIVE, updated.getStatus());
            assertEquals("acc-101", updated.getId());

            verify(accountRepository, times(1)).save(sampleAccount);
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when updating non-existent profile")
        void testUpdateProfile_NotFound() {
            when(accountRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            UpdateProfileRequest updateRequest = new UpdateProfileRequest("Johnny", "Silva", "0779998888");

            assertThrows(
                    AccountNotFoundException.class,
                    () -> accountService.updateProfile("nonexistent@example.com", updateRequest)
            );
        }
    }

    @Nested
    @DisplayName("Account Lookup Tests")
    class LookupTests {

        @Test
        @DisplayName("Should successfully find account by ID")
        void testGetAccountById_Success() {
            when(accountRepository.findById("acc-101")).thenReturn(Optional.of(sampleAccount));

            AccountResponse response = accountService.getAccountById("acc-101");

            assertNotNull(response);
            assertEquals("acc-101", response.getId());
            assertEquals("John", response.getFirstName());
            assertEquals("john@example.com", response.getEmail());
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when account ID is not found")
        void testGetAccountById_NotFound() {
            when(accountRepository.findById("invalid-id")).thenReturn(Optional.empty());

            assertThrows(
                    AccountNotFoundException.class,
                    () -> accountService.getAccountById("invalid-id")
            );
        }
    }
}
