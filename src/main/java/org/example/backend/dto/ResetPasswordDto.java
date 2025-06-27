package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotBlank(message = "OTP không được để trống!")
    private String otp;

    @NotBlank(message = "Email không được để trống!")
    private String email;

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    private String newPassword;
}