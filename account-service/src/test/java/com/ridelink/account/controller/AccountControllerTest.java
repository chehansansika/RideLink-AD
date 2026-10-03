package com.ridelink.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.dto.*;
import com.ridelink.account.exception.*;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccountResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = new AccountResponse(
                "acc-001",
                "John",
                "Perera",
                "john@example.com",
                "0771234567",
                Role.PASSENGER,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/accounts/register - Success returns 201 Created")
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John",
                "Perera",
                "john@example.com",
                "Password123",
                "0771234567",
                Role.PASSENGER
        );

        when(accountService.register(any(RegisterRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("acc-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("PASSENGER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/accounts/register - Duplicate email returns 409 Conflict")
    void testRegister_DuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John",
                "Perera",
                "john@example.com",
                "Password123",
                "0771234567",
                Role.PASSENGER
        );

        when(accountService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("Email is already registered: john@example.com"));

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email is already registered: john@example.com"));
    }

    @Test
    @DisplayName("POST /api/accounts/register - Invalid input returns 400 Bad Request")
    void testRegister_InvalidInput() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest(
                "", // Blank first name
                "",
                "invalid-email-format",
                "123", // Too short password
                "",
                null // Null role
        );

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("POST /api/accounts/login - Success returns 200 OK with token")
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "Password123");
        LoginResponse response = new LoginResponse("mock-jwt-token", "acc-001", "john@example.com", Role.PASSENGER);

        when(accountService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.accountId").value("acc-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("PASSENGER"));
    }

    @Test
    @DisplayName("POST /api/accounts/login - Invalid credentials returns 401 Unauthorized")
    void testLogin_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "WrongPassword");

        when(accountService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/accounts/login - Suspended account returns 403 Forbidden")
    void testLogin_SuspendedAccount() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "Password123");

        when(accountService.login(any(LoginRequest.class)))
                .thenThrow(new AccountSuspendedException("Account has been suspended. Please contact support."));

        mockMvc.perform(post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Account has been suspended. Please contact support."));
    }

    @Test
    @DisplayName("GET /api/accounts/profile - Authenticated user returns 200 OK")
    void testGetProfile_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "john@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSENGER"))
        );

        when(accountService.getProfile("john@example.com")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/accounts/profile").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("acc-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("PUT /api/accounts/profile - Success updates profile")
    void testUpdateProfile_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "john@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSENGER"))
        );

        UpdateProfileRequest updateRequest = new UpdateProfileRequest("Johnny", "Silva", "0779998888");
        sampleResponse.setFirstName("Johnny");
        sampleResponse.setLastName("Silva");
        sampleResponse.setPhone("0779998888");

        when(accountService.updateProfile(eq("john@example.com"), any(UpdateProfileRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/accounts/profile")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.lastName").value("Silva"))
                .andExpect(jsonPath("$.phone").value("0779998888"));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} - Found returns 200 OK")
    void testGetAccountById_Success() throws Exception {
        when(accountService.getAccountById("acc-001")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/accounts/acc-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("acc-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} - Not found returns 404 Not Found")
    void testGetAccountById_NotFound() throws Exception {
        when(accountService.getAccountById("invalid-id"))
                .thenThrow(new AccountNotFoundException("Account not found with ID: invalid-id"));

        mockMvc.perform(get("/api/accounts/invalid-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account not found with ID: invalid-id"));
    }
}
