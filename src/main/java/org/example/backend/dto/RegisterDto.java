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
public class RegisterDto {

    @NotBlank(message = "email không được để trống!")
    @Email
    private String email;

    @NotBlank(message = "Mật kẩu không được để trống!")
    @Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
            message = "Mật khẩu phải có chữ in hoa, chữ thường, số và ký tự đặc biệt!")
    private String password;

    @NotBlank(message = "Mật Khẩu không được để trống!")
    private String passwordConfirm;

    @NotBlank(message = "Họ tên không được để trống!")
    @Size(min = 3,message = "Họ tên ít nhất 3 ký tự!")
    private String name;
}
