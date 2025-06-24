package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String name;
    @Pattern(regexp = "^(\\+84|0)([0-9]{9})$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private String dateOfBirth;
    private MultipartFile avatar;
}
