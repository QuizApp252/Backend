package org.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.model.User;
import org.example.backend.service.IAuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;


public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final IAuthService authService;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, IAuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = authService.handleGoogleLogin(email, name);
        String token = jwtUtil.generateToken(user);

        String redirectUrl = "http://localhost:3000/oauth2/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}

