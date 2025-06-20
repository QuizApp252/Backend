package org.example.backend.service;

import org.example.backend.dto.RegisterDto;
import org.example.backend.model.User;

import java.util.Optional;

public interface IAuthService {
    void register(RegisterDto request);
    Optional<User> findByEmail(String email);
}
