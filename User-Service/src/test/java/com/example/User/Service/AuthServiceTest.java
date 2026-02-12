package com.example.User.Service;

import com.example.springjwt.entity.Role;
import com.example.springjwt.entity.User;
import com.example.springjwt.model.request.LoginRequest;
import com.example.springjwt.model.request.RegisterRequest;
import com.example.springjwt.repositories.OtpRepository;
import com.example.springjwt.repositories.TokenRepository;
import com.example.springjwt.repositories.UserRepository;
import com.example.springjwt.services.AuthService;
import com.example.springjwt.services.JwtService;
import com.example.springjwt.services.TextbeltOtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private TextbeltOtpService textbeltOtpService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("12345");
        request.setPhoneNumber("01012345678");

        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(true);

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
        when(jwtService.createToken(eq(user), anyMap())).thenReturn("mocked-jwt");
        doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        doNothing().when(textbeltOtpService).sendOtp("01012345678");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testLoginAccountNotActivated() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("12345");
        request.setPhoneNumber("01012345678");

        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(false);

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
        doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        var response = authService.login(request);

        assertNull(response.getToken());
        assertEquals("Account not activated", response.getMessage());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPhoneNumber("01012345678");
        request.setPassword("pass");
        request.setRole(Role.USER);

        when(userRepository.findUserByEmail("newuser@example.com")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");

        User savedUser = new User();
        savedUser.setEmail("newuser@example.com");
        savedUser.setPhoneNumber("01012345678");
        savedUser.setPassword("encoded-pass");
        savedUser.setRole(Role.USER);
        savedUser.setEnabled(false);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(textbeltOtpService).sendOtp("01012345678");

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("OTP sent to your phone", response.getMessage());
        verify(textbeltOtpService, times(1)).sendOtp("01012345678");
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findUserByEmail("existing@example.com")).thenReturn(new User());

        var response = authService.register(request);

        assertNull(response.getToken());
        assertEquals("Email already registered", response.getMessage());
    }

    @Test
    void testActivateAccountSuccess() {
        User user = new User();
        user.setPhoneNumber("01012345678");

        when(userRepository.findByPhoneNumber("01012345678")).thenReturn(Optional.of(user));
        when(textbeltOtpService.verifyOtp("01012345678", "123456")).thenReturn(true);

        boolean result = authService.activateAccount("01012345678", "123456");

        assertTrue(result);
        verify(userRepository).save(user);
    }
}