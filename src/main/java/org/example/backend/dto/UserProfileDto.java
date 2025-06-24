package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private int id;
    private String email;
    private String provider;
    private String role;
    private String name;
    private String avatar;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private boolean status;
    private LocalDateTime createAt;
    private boolean isDelete;
}
