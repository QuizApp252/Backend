package org.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private Provider provider = Provider.LOCAL;
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;
    private String name;
    private String avatar;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "phone_number")
    private String phoneNumber;
    private boolean status = false;
    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();
    @Column(name = "is_delete")
    private boolean isDelete = false;
    public enum Provider{
        GOOGLE,LOCAL
    }
    public enum Role{
        ADMIN,USER
    }
}
