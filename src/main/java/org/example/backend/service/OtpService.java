package org.example.backend.service;

import org.example.backend.exception.CustomBadRequestException;
import org.example.backend.exception.CustomRequestTooSoonException;
import org.example.backend.model.Otp;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.example.backend.repository.IOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService implements IOtpService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private IOtpRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IAuthRepository authRepository;

    @Override
    public Optional<Otp> findByUserEmail(String email) {
        return otpRepository.findByUserEmail(email);
    }

    public String generateOtp(User user) {
        Optional<Otp> optionalOtp = otpRepository.findByUserEmail(user.getEmail());
        if (optionalOtp.isPresent()) {
            Otp existingOtp = optionalOtp.get();
            if (Duration.between(existingOtp.getCreatedAt(), LocalDateTime.now()).toSeconds() < 60) {
                throw new CustomRequestTooSoonException("otp", "Bạn chỉ có thể gửi lại OTP sau 60 giây.");
            }
            otpRepository.delete(existingOtp);
        }
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1000000));
        Otp otpToken = new Otp();
        otpToken.setOtpToken(passwordEncoder.encode(otp));
        otpToken.setUser(user);
        otpToken.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpToken);
        return otp;
    }

    @Override
    public void sendOtpToEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP xác thực email");
        message.setText("OTP của bạn để xác minh email là: " + otp + "\nCó hiệu lực trong 5 phút.");
        message.setFrom(fromEmail);
        mailSender.send(message);
    }

    @Override
    public boolean verifyOtp(String email, String rawOtp) {
        Optional<User> optionalUser = authRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new CustomBadRequestException("email", "Email không tồn tại!");
        }

        User user = optionalUser.get();

        Optional<Otp> optionalOtp = otpRepository.findByUserEmail(email);
        if (optionalOtp.isEmpty()) {
            throw new CustomBadRequestException("otp", "OTP không tồn tại, vui lòng yêu cầu mã mới.");
        }

        Otp otpToken = optionalOtp.get();

        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpToken);
            throw new CustomBadRequestException("otp", "OTP đã hết hạn, vui lòng yêu cầu mã mới.");
        }

        if (passwordEncoder.matches(rawOtp, otpToken.getOtpToken())) {
            user.setStatus(true);
            authRepository.save(user);
            otpRepository.delete(otpToken);
            return true;
        } else {
            int attempts = otpToken.getAttempts() + 1;
            otpToken.setAttempts(attempts);

            if (attempts >= 3) {
                otpRepository.delete(otpToken);
                throw new CustomBadRequestException("otp", "Bạn đã nhập sai quá 3 lần. Hãy yêu cầu OTP mới.");
            } else {
                otpRepository.save(otpToken);
                throw new CustomBadRequestException("otp", "OTP không đúng. Bạn còn " + (3 - attempts) + " lần thử.");
            }
        }
    }
}
