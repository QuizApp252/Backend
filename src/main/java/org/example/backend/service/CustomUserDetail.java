package org.example.backend.service;

import org.example.backend.exception.CustomAccountLockedException;
import org.example.backend.exception.CustomDeletedAccountException;
import org.example.backend.exception.CustomNotFoundException;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetail implements UserDetailsService {

    @Autowired
    private IAuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new CustomNotFoundException("email", "Email chưa được đăng ký!"));
        if (!user.isStatus()) {
            throw new CustomAccountLockedException("isStatus", "Tài khoản chưa được kích hoạt!");
        }
        if (user.isDelete()) {
            throw new CustomDeletedAccountException("isDelete", "Tài khoản đã bị khóa do vi phạm!");
        }
        String password = user.getPassword();
        if (password == null || password.isBlank()) {
            // Gán mật khẩu giả nếu là user đăng nhập bằng Google
            password = "oauth2_placeholder";
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
