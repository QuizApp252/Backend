package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.ApiResponseDto;
import org.example.backend.dto.RegisterDto;
import org.example.backend.dto.ResendOtpDto;
import org.example.backend.dto.VerifyOtpDto;
import org.example.backend.exception.NotFoundException;
import org.example.backend.model.Otp;
import org.example.backend.model.User;
import org.example.backend.service.IAuthService;
import org.example.backend.service.IOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @Autowired
    private IOtpService otpService;

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
            throw new NotFoundException("email", "Không tìm thấy email!");
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


}
