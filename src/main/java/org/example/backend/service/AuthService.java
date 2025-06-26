package org.example.backend.service;

import org.example.backend.config.JwtUtil;
import org.example.backend.dto.LoginDto;
import org.example.backend.dto.RegisterDto;
import org.example.backend.exception.*;
import org.example.backend.model.User;
import org.example.backend.repository.IAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements IAuthService{
    @Autowired
    private IAuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Optional<User> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }
    @Override
    public void register(RegisterDto request){
        if(authRepository.existsByEmail(request.getEmail())){
            throw new CustomConflictException("email","Email đã được sử dụng!");
        }
        if(!request.getPassword().equals(request.getPasswordConfirm())){
            throw new CustomBadRequestException("passwordConfirm","Mật khẩu không khớp!");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        authRepository.save(user);
    }
    @Override
    public String login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtUtil.generateToken(authRepository.findByEmail(userDetails.getUsername()).get());
        } catch (BadCredentialsException e) {
            throw new CustomBadRequestException("password", "Mật khẩu không đúng!");
        } catch (DisabledException e) {
            throw new CustomAccountLockedException("isStatus", "Tài khoản chưa được kích hoạt!");
        } catch (LockedException e) {
            throw new CustomDeletedAccountException("isDelete", "Tài khoản đã bị khóa!");
        }
    }
    @Override
    public User handleGoogleLogin(String email, String name) {
        Optional<User> optionalUser = authRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setProvider(User.Provider.GOOGLE);
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            String fakePassword = passwordEncoder.encode(UUID.randomUUID().toString());
            user.setPassword(fakePassword);
            user.setProvider(User.Provider.GOOGLE);
            user.setStatus(true); // Tài khoản Google không cần xác thực OTP
        }
        return authRepository.save(user);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
