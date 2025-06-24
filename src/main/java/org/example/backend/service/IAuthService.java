package org.example.backend.service;

import org.example.backend.dto.LoginDto;
import org.example.backend.dto.RegisterDto;
import org.example.backend.model.User;

import java.util.Optional;

public interface IAuthService {
    void register(RegisterDto request);
    Optional<User> findByEmail(String email);
    String login(LoginDto loginDto);
    User handleGoogleLogin(String email, String name);
    boolean checkPassword(String rawPassword, String encodedPassword);
    String encodePassword(String password);
}
