package com.example.Todo.Service.Services;

import com.example.Todo.Service.DTO.UserTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "User-Service", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/rest/auth/checkToken")
    UserTokenResponse checkToken(
            @RequestHeader("Authorization") String token
    );
}