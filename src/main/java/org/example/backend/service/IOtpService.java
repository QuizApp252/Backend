package org.example.backend.service;

import org.example.backend.model.Otp;
import org.example.backend.model.User;

import java.util.Optional;

public interface IOtpService {
    String generateOtp(User user);
    void sendOtpToEmail(String email, String otp);
    boolean verifyOtp(String email,String otp);
    Optional<Otp> findByUserEmail(String email);
}
