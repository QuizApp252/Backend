package org.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "Email không được bỏ trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;
    @NotBlank(message = "Mật khẩu không được bỏ trống!")
    private String password;
}
