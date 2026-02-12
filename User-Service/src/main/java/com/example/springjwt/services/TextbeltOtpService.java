package com.example.springjwt.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TextbeltOtpService {

    private final RestTemplate restTemplate;

    @Value("${textbelt.apiKey}")
    private String apiKey;

    private final Map<String, String> otpStore = new HashMap<>();

    public TextbeltOtpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String sendOtp(String phoneNumber) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 أرقام
        otpStore.put(phoneNumber, otp);

        String url = "https://textbelt.com/text";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "phone=" + phoneNumber + "&message=Your OTP is: " + otp + "&key=" + apiKey;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        System.out.println("OTP sent to " + phoneNumber + ": " + otp); // للـ debug

        return otp;
    }

    // تحقق OTP
    public boolean verifyOtp(String phoneNumber, String code) {
        String validOtp = otpStore.get(phoneNumber);
        if(validOtp != null && validOtp.equals(code)) {
            otpStore.remove(phoneNumber);
            return true;
        }
        return false;
    }
}
