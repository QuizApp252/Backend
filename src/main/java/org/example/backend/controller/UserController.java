package org.example.backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.validation.Valid;
import org.example.backend.dto.ApiResponseDto;
import org.example.backend.dto.ChangePasswordDto;
import org.example.backend.dto.UserProfileDto;
import org.example.backend.dto.UserUpdateDto;
import org.example.backend.exception.CustomNotFoundException;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.example.backend.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    @Autowired
    private IAuthRepository userRepository;
    @Autowired
    private IAuthService authService;
    @Autowired
    private Cloudinary cloudinary;
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomNotFoundException("user", "Không tìm thấy người dùng"));

            UserProfileDto profileDto = getUserProfileDto(user);
            return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Lấy thông tin hồ sơ thành công!", profileDto));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin hồ sơ!", null));
        }
    }

    private static UserProfileDto getUserProfileDto(User user) {
        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setId(user.getId());
        profileDto.setEmail(user.getEmail());
        profileDto.setName(user.getName());
        profileDto.setStatus(user.isStatus());
        profileDto.setAvatar(user.getAvatar());
        profileDto.setRole(user.getRole().name());
        profileDto.setProvider(user.getProvider().name());
        profileDto.setDelete(user.isDelete());
        profileDto.setCreateAt(user.getCreateAt());
        profileDto.setDateOfBirth(user.getDateOfBirth());
        profileDto.setPhoneNumber(user.getPhoneNumber());
        return profileDto;
    }
    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@Valid @ModelAttribute UserUpdateDto dto,
                                           BindingResult bindingResult) {
        // Xử lý lỗi validate
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Lỗi xác thực dữ liệu!", errors));
        }

        try {
            // Lấy email từ SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // Tìm user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomNotFoundException("user", "Không tìm thấy người dùng"));

            // Cập nhật các trường
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

            if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isBlank()) {
                try {
                    user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth())); // yyyy-MM-dd
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Ngày sinh không hợp lệ", null));
                }
            }

            // Upload avatar nếu có file
            if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(
                        dto.getAvatar().getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                user.setAvatar(imageUrl);
            }

            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Cập nhật thông tin thành công", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật thông tin", null));
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Lỗi xác thực!", errors));
        }

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomNotFoundException("user", "Không tìm thấy người dùng"));

            if (!authService.checkPassword(dto.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không đúng!", null));
            }

            user.setPassword(authService.encodePassword(dto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponseDto(HttpStatus.OK, "Đổi mật khẩu thành công!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi đổi mật khẩu!", null));
        }
    }
}
