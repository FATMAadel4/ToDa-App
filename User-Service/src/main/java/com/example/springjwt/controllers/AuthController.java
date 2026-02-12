package com.example.springjwt.controllers;

import com.example.springjwt.model.request.AuthenticationResponse;
import com.example.springjwt.model.request.LoginRequest;
import com.example.springjwt.model.request.RegisterRequest;
import com.example.springjwt.model.response.UserTokenResponse;
import com.example.springjwt.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthenticationResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<AuthenticationResponse> activateAccount(
            @RequestParam String phoneNumber,
            @RequestParam String otp) {

        AuthenticationResponse response = authService.activateAccountWithToken(phoneNumber, otp);

        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam String phoneNumber) {
        try {
            authService.generateForgetPasswordOtp(phoneNumber);
            return ResponseEntity.ok("OTP sent to your phone");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestParam String phoneNumber,
            @RequestParam String otp,
            @RequestParam String newPassword) {

        try {
            boolean changed = authService.changePassword(phoneNumber, otp, newPassword);
            if (changed) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP or phone number");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/regenerateOtp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String phoneNumber) {
        try {
            authService.regenerateOtpByPhone(phoneNumber);
            return ResponseEntity.ok("New OTP sent to your phone");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/checkToken")
    public ResponseEntity<UserTokenResponse> checkToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        UserTokenResponse response = authService.validateTokenAndGetUser(authHeader);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
