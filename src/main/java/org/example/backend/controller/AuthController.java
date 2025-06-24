package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.config.JwtUtil;
import org.example.backend.dto.*;
import org.example.backend.exception.CustomNotFoundException;
import org.example.backend.model.User;
import org.example.backend.service.IAuthService;
import org.example.backend.service.IOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @Autowired
    private IOtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Lỗi xác thực dữ liệu!", errors));
        }
        authService.register(request);
        String otp = otpService.generateOtp(authService.findByEmail(request.getEmail()).get());
        otpService.sendOtpToEmail(request.getEmail(), otp);
        Map<String, String> responseData = Map.of("userId", request.getEmail());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK, "Đăng ký thành công. Vui lòng kiểm tra email để xác thực!", responseData));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto request) {
        boolean verify = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (verify) {
            return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Xác thực thành công!", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "OTP không hợp lệ!", null));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpDto request) {
        Optional<User> optionalUser = authService.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new CustomNotFoundException("email", "Không tìm thấy email!");
        }

        User user = optionalUser.get();

        if (user.isStatus()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponseDto(HttpStatus.BAD_REQUEST, "Tài khoản đã được xác thực!", null)
            );
        }

        String otp = otpService.generateOtp(user);
        otpService.sendOtpToEmail(user.getEmail(), otp);

        return ResponseEntity.ok(
                new ApiResponseDto(HttpStatus.OK, "OTP mới đã được gửi lại.", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Lỗi xác thực dữ liệu!", errors));
        }
        try {
            String token = authService.login(loginDto);
            return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Đăng nhập thành công!", Map.of("token", token)));
        } catch (Exception ex) {
            // Bạn có thể thêm logger tại đây nếu muốn log lỗi

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng!", null));
        }
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        User user = authService.handleGoogleLogin(email, name);
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Đăng nhập Google thành công!", Map.of("token", token)));
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<?> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Đăng nhập Google thất bại!", null));
    }
}
