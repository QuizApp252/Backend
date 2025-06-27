package org.example.backend.service;

import org.example.backend.exception.CustomBadRequestException;
import org.example.backend.exception.CustomRequestTooSoonException;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

@Service
public class OtpService implements IOtpService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IAuthRepository authRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String OTP_PREFIX = "OTP:"; // Key prefix trong Redis
    private static final String FAIL_PREFIX = "OTP_FAIL:"; // Key prefix trong Redis

    public String generateOtp(User user) {
        String email = user.getEmail();
        String redisKey = OTP_PREFIX + email;
        String rateLimitKey = "rateLimit:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
                throw new CustomRequestTooSoonException("otp", "Bạn chỉ có thể gửi lại OTP sau 60 giây.");
            }
            redisTemplate.delete(redisKey);
        }
        String rawOtp = String.format("%06d", new SecureRandom().nextInt(999999));
        redisTemplate.opsForValue().set(redisKey, rawOtp, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(rateLimitKey, "1", Duration.ofMinutes(1));
        return rawOtp;
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
        String redisKey = OTP_PREFIX + email;
        String savedOtp = redisTemplate.opsForValue().get(redisKey);
        String failKey = FAIL_PREFIX + email;

        if (savedOtp == null) {
            throw new CustomBadRequestException("otp", "OTP không tồn tại hoặc đã hết hạn.");
        }
        Optional<User> optionalUser = authRepository.findByEmail(email);
        if (savedOtp.equals(rawOtp)) {
            optionalUser.get().setStatus(true);
            authRepository.save(optionalUser.get());
            redisTemplate.delete(redisKey); // Xóa sau khi dùng
            redisTemplate.delete(failKey);
            return true;
        } else {
            Long attempts = redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, Duration.ofMinutes(5));
            if (attempts != null && attempts >= 3) {
                redisTemplate.delete(redisKey);
                redisTemplate.delete(failKey);
                throw new CustomBadRequestException("otp", "Bạn đã nhập sai quá 3 lần. Hãy yêu cầu mã OTP mới.");
            }

            throw new CustomBadRequestException("otp", "OTP không đúng. Bạn còn " + (3 - attempts) + " lần thử.");
        }
    }
}
