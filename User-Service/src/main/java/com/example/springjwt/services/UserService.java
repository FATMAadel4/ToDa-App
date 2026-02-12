package com.example.springjwt.services;

import com.example.springjwt.DTO.UpdateUserRequest;
import com.example.springjwt.entity.User;
import com.example.springjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

     public String deleteUser(String email, String password, String token) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);


        if (!jwtService.isTokenValid(token, userDetails)) {
            return "Invalid or expired token";
        }

        User user = userRepository.findUserByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return "User not found or invalid password";
        }

        userRepository.delete(user);
        return "User deleted successfully";
    }


    public String updateUser(UpdateUserRequest request, String token) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());


        if (!jwtService.isTokenValid(token, userDetails)) {
            return "Invalid or expired token";
        }

        User user = userRepository.findUserByEmail(request.getEmail());
        if (user == null) {
            return "User not found";
        }

         if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getNewEmail() != null && !request.getNewEmail().isEmpty()) {
            user.setEmail(request.getNewEmail());
        }

        userRepository.save(user);
        return "User updated successfully";
    }


    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findUserByEmail(email));
    }
}
