package com.example.springjwt.services;

import com.example.springjwt.entity.Token;
import com.example.springjwt.entity.TokenType;
import com.example.springjwt.entity.User;
import com.example.springjwt.model.request.AuthenticationResponse;
import com.example.springjwt.model.request.LoginRequest;
import com.example.springjwt.model.request.RegisterRequest;
import com.example.springjwt.model.response.UserTokenResponse;
import com.example.springjwt.repositories.TokenRepository;
import com.example.springjwt.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TextbeltOtpService textbeltOtpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;



    public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findUserByEmail(request.getEmail());
        if (user == null) {
            return new AuthenticationResponse(null, "Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthenticationResponse(null, "Invalid email or password");
        }

        if (!user.isEnabled()) {
            textbeltOtpService.sendOtp(user.getPhoneNumber());
            return new AuthenticationResponse(null, "Account not activated. New OTP sent to your phone.");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        String jwtToken = jwtService.createToken(user, extraClaims);
        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, "Login successful");
    }


    public AuthenticationResponse register(RegisterRequest request) {

        Optional<User> existingUser = Optional.ofNullable(userRepository.findUserByEmail(request.getEmail()));

        if (existingUser.isPresent()) {
            return new AuthenticationResponse(null, "Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        textbeltOtpService.sendOtp(savedUser.getPhoneNumber());

        return new AuthenticationResponse(null, "OTP sent to your phone");
    }


    public boolean activateAccount(String phoneNumber, String otpCode) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        return userOpt.map(user -> {
            boolean isVerified = textbeltOtpService.verifyOtp(phoneNumber, otpCode);
            if (isVerified) {
                user.setEnabled(true);
                userRepository.save(user);
                return true;
            }
            return false;
        }).orElse(false);
    }


    public AuthenticationResponse activateAccountWithToken(String phoneNumber, String otpCode) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        return userOpt.map(user -> {
            boolean isVerified = textbeltOtpService.verifyOtp(phoneNumber, otpCode);
            if (isVerified) {
                user.setEnabled(true);
                userRepository.save(user);

                Map<String, Object> extraClaims = new HashMap<>();
                String jwtToken = jwtService.createToken(user, extraClaims);
                saveUserToken(user, jwtToken);

                return new AuthenticationResponse(jwtToken, user.getEmail());
            }
            return new AuthenticationResponse("Invalid OTP");
        }).orElse(new AuthenticationResponse("Phone number not found"));
    }


    public boolean generateForgetPasswordOtp(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        return userOpt.map(user -> {
            textbeltOtpService.sendOtp(user.getPhoneNumber());
            return true;
        }).orElse(false);
    }


    public boolean changePassword(String phoneNumber, String otpCode, String newPassword) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        return userOpt.map(user -> {
            boolean isVerified = textbeltOtpService.verifyOtp(phoneNumber, otpCode);
            if (isVerified) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public boolean regenerateOtpByPhone(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        return userOpt.map(user -> {
            textbeltOtpService.sendOtp(user.getPhoneNumber());
            return true;
        }).orElse(false);
    }


    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public boolean validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return false;

        String token = authHeader.substring(7);
        return isTokenValid(token);
    }

    private boolean isTokenValid(String token) {
        try {
            Claims claims = jwtService.parseJwtClaims(token);
            String email = claims.getSubject();
            Optional<User> userOpt = Optional.ofNullable(userRepository.findUserByEmail(email));
            return userOpt.isPresent() && !jwtService.isTokenExpired(claims.getExpiration());
        } catch (Exception e) {
            return false;
        }
    }
    public UserTokenResponse validateTokenAndGetUser(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.substring(7);
            Claims claims = jwtService.parseJwtClaims(token);

            if (jwtService.isTokenExpired(claims.getExpiration())) {
                return null;
            }

            String email = claims.getSubject();
            User user = userRepository.findUserByEmail(email);

            if (user == null || !user.isEnabled()) {
                return null;
            }

            return new UserTokenResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().toString()
            );

        } catch (Exception e) {
            return null;
        }
    }
}