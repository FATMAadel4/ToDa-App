package com.example.springjwt.model.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String email;
    private String message;


    public AuthenticationResponse(String accessToken, String email) {
        this.accessToken = accessToken;
        this.email = email;
    }

     public AuthenticationResponse(String message) {
        this.message = message;
    }

    public String getToken() {
        return accessToken;
    }

    public String getMessage() {
        return message;
    }
}
