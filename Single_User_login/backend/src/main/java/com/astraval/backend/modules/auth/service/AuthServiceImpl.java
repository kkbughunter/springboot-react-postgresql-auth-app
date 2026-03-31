package com.astraval.backend.modules.auth.service;

import com.astraval.backend.common.exception.InvalidCredentialsException;
import com.astraval.backend.common.exception.UserAlreadyExistsException;
import com.astraval.backend.common.util.JwtUtil;
import com.astraval.backend.modules.auth.mapper.AuthMapper;
import com.astraval.backend.modules.auth.request.LoginRequest;
import com.astraval.backend.modules.auth.request.RegisterRequest;
import com.astraval.backend.modules.auth.response.AuthResponse;
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
    public AuthResponse register(RegisterRequest request) {
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

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getUserId().toString());
        return authMapper.toAuthResponse(savedUser, request.getFullName(), token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String fullName = userDetailRepository.findByUserId(user.getUserId())
                .map(UserDetail::getFullName)
                .orElse(null);

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId().toString());
        return authMapper.toAuthResponse(user, fullName, token);
    }
}
