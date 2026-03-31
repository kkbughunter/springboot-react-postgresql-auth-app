package com.astraval.backend.modules.auth.service;

import com.astraval.backend.common.exception.InvalidCredentialsException;
import com.astraval.backend.common.exception.UserAlreadyExistsException;
import com.astraval.backend.common.util.JwtUtil;
import com.astraval.backend.modules.auth.mapper.AuthMapper;
import com.astraval.backend.modules.auth.request.LoginRequest;
import com.astraval.backend.modules.auth.request.RegisterRequest;
import com.astraval.backend.modules.auth.response.AuthResult;
import com.astraval.backend.modules.auth.response.AuthTokenPair;
import com.astraval.backend.modules.user.entity.User;
import com.astraval.backend.modules.user.entity.UserDetail;
import com.astraval.backend.modules.user.repo.UserDetailRepository;
import com.astraval.backend.modules.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setCreatedBy("SYSTEM");
        user.setModifiedBy("SYSTEM");
        User savedUser = userRepository.save(user);

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(savedUser);
        userDetail.setFullName(request.getFullName());
        userDetail.setIsActive(true);
        userDetail.setCreatedBy("SYSTEM");
        userDetail.setModifiedBy("SYSTEM");
        userDetailRepository.save(userDetail);

        String accessToken  = jwtUtil.generateAccessToken(savedUser.getEmail(), savedUser.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());
        return authMapper.toAuthResult(savedUser, request.getFullName(), null, accessToken, refreshToken);
    }

    @Override
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserDetail detail = userDetailRepository.findByUserId(user.getUserId()).orElse(null);
        String fullName = detail != null ? detail.getFullName() : null;
        String gender   = detail != null ? detail.getGender()   : null;

        String accessToken  = jwtUtil.generateAccessToken(user.getEmail(), user.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return authMapper.toAuthResult(user, fullName, gender, accessToken, refreshToken);
    }

    @Override
    public AuthTokenPair refreshTokens(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        String newAccessToken  = jwtUtil.generateAccessToken(email, user.getUserId().toString());
        String newRefreshToken = jwtUtil.generateRefreshToken(email); // rotation
        return AuthTokenPair.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
