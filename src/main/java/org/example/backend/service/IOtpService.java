package org.example.backend.service;

import org.example.backend.model.User;

public interface IOtpService {
    String generateOtp(User user);

    void sendOtpToEmail(String email, String otp);

    boolean verifyOtp(String email, String otp);

}
