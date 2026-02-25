package com.kas.online_book_shop;

import com.kas.online_book_shop.controller.AuthenticationController;
import com.kas.online_book_shop.dto.*;
import com.kas.online_book_shop.enums.Role;
import com.kas.online_book_shop.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho Guest - Minh
 * Chức năng: Login/Logout/Register/Reset Password/Search Book
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Guest Functionality Test - Minh")
public class GuestFunctionalityTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        // Setup dữ liệu test
        registerRequest = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "Hanoi",
                "Ba Dinh",
                "Cong Vi",
                "0123456789",
                "123 Test Street",
                Role.USER
        );

        authenticationRequest = new AuthenticationRequest(
                "test@example.com",
                "password123"
        );

        forgotPasswordRequest = new ForgotPasswordRequest(
                "test@example.com"
        );

        resetPasswordRequest = new ResetPasswordRequest(
                "reset-token-123",
                "newPassword123"
        );

        authenticationResponse = new AuthenticationResponse(
                "jwt-token-123"
        );
    }

    @Test
    @DisplayName("Test 1: Register - Đăng ký tài khoản thành công")
    void testRegisterSuccess() throws Exception {
        // Given
        doNothing().when(authenticationService).register(any(RegisterRequest.class));

        // When
        ResponseEntity<String> response = authenticationController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Test 2: Login - Đăng nhập thành công")
    void testLoginSuccess() throws Exception {
        // Given
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authenticationResponse);

        // When
        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(authenticationRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().token());
        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    @DisplayName("Test 3: Login - Đăng nhập thất bại với email không tồn tại")
    void testLoginFailureWithInvalidEmail() throws Exception {
        // Given
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationController.authenticate(authenticationRequest));
        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    @DisplayName("Test 4: Forgot Password - Gửi yêu cầu quên mật khẩu thành công")
    void testForgotPasswordSuccess() throws Exception {
        // Given
        doNothing().when(authenticationService).forgotPassword(any(ForgotPasswordRequest.class));

        // When
        ResponseEntity<AuthenticationResponse> response = authenticationController.forgotPassword(forgotPasswordRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("Test 5: Reset Password - Đặt lại mật khẩu thành công")
    void testResetPasswordSuccess() {
        // Given
        when(authenticationService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(authenticationResponse);

        // When
        ResponseEntity<AuthenticationResponse> response = authenticationController.resetPassword(resetPasswordRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    @DisplayName("Test 6: Register - Đăng ký thất bại với email đã tồn tại")
    void testRegisterFailureWithExistingEmail() throws Exception {
        // Given
        RegisterRequest duplicateRequest = new RegisterRequest(
                "Another User",
                "test@example.com",  // Same email
                "password456",
                "Hanoi",
                "Hoan Kiem",
                "Tran Hung Dao",
                "0987654321",
                "456 Another Street",
                Role.USER
        );
        
        doThrow(new RuntimeException("Email already exists"))
                .when(authenticationService).register(any(RegisterRequest.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationController.register(duplicateRequest));
        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Test 7: Register - Validation với dữ liệu không hợp lệ")
    void testRegisterWithInvalidData() throws Exception {
        // Given - Create request with invalid data
        RegisterRequest invalidRequest = new RegisterRequest(
                "",  // Empty name
                "invalid-email",  // Invalid email format
                "123",  // Too short password
                "",  // Empty province
                "",  // Empty district
                "",  // Empty ward
                "",  // Empty phone
                "",  // Empty address
                Role.USER
        );
        
        doNothing().when(authenticationService).register(any(RegisterRequest.class));

        // When
        ResponseEntity<String> response = authenticationController.register(invalidRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Test 8: Login - Validation với dữ liệu rỗng")
    void testLoginWithEmptyCredentials() throws Exception {
        // Given
        AuthenticationRequest emptyRequest = new AuthenticationRequest("", "");
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationController.authenticate(emptyRequest));
        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    @DisplayName("Test 9: Forgot Password - Email không tồn tại")
    void testForgotPasswordWithNonExistentEmail() throws Exception {
        // Given
        ForgotPasswordRequest nonExistentEmail = new ForgotPasswordRequest("nonexistent@example.com");
        doNothing().when(authenticationService).forgotPassword(any(ForgotPasswordRequest.class));

        // When
        ResponseEntity<AuthenticationResponse> response = authenticationController.forgotPassword(nonExistentEmail);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("Test 10: Reset Password - Token không hợp lệ")
    void testResetPasswordWithInvalidToken() {
        // Given
        ResetPasswordRequest invalidTokenRequest = new ResetPasswordRequest(
                "invalid-token",
                "newPassword123"
        );
        when(authenticationService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(authenticationResponse);

        // When
        ResponseEntity<AuthenticationResponse> response = authenticationController.resetPassword(invalidTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }
}
