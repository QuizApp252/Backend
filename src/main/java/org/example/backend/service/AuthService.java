package org.example.backend.service;

import org.example.backend.dto.RegisterDto;
import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ConflictException;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService{
    @Autowired
    private IAuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterDto request){
        if(authRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("email","Email đã được sử dụng!");
        }
        if(!request.getPassword().equals(request.getPasswordConfirm())){
            throw new BadRequestException("passwordConfirm","Mật khẩu không khớp!");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        authRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }
}
