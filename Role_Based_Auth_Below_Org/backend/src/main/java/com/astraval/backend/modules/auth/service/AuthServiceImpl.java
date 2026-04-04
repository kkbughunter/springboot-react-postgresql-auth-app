package com.astraval.backend.modules.auth.service;

import com.astraval.backend.common.exception.InvalidCredentialsException;
import com.astraval.backend.common.exception.OrganizationNotFoundException;
import com.astraval.backend.common.exception.UserAlreadyExistsException;
import com.astraval.backend.common.util.JwtUtil;
import com.astraval.backend.modules.auth.request.LoginRequest;
import com.astraval.backend.modules.auth.request.RegisterRequest;
import com.astraval.backend.modules.auth.response.AuthResult;
import com.astraval.backend.modules.auth.response.AuthTokenPair;
import com.astraval.backend.modules.organization.entity.Organization;
import com.astraval.backend.modules.organization.repo.OrganizationRepository;
import com.astraval.backend.modules.role.entity.Role;
import com.astraval.backend.modules.role.repo.RoleRepository;
import com.astraval.backend.modules.user.entity.User;
import com.astraval.backend.modules.user.entity.UserDetail;
import com.astraval.backend.modules.user.entity.UserRoleMap;
import com.astraval.backend.modules.user.repo.UserDetailRepository;
import com.astraval.backend.modules.user.repo.UserRepository;
import com.astraval.backend.modules.user.repo.UserRoleMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (organizationRepository.existsByOrgCode(request.getOrgCode())) {
            throw new UserAlreadyExistsException("Organization code '" + request.getOrgCode() + "' is already taken");
        }
        if (userDetailRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        Organization org = new Organization();
        org.setOrgName(request.getOrgName());
        org.setOrgCode(request.getOrgCode().toLowerCase());
        Organization savedOrg = organizationRepository.save(org);

        User user = new User();
        user.setOrg(savedOrg);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        UserDetail detail = new UserDetail();
        detail.setUser(savedUser);
        detail.setFullName(request.getFullName());
        detail.setEmail(request.getEmail().toLowerCase());
        detail.setPhone(request.getPhone());
        detail.setGender(null);
        userDetailRepository.save(detail);

        Role adminRole = roleRepository.findById(Role.ADMIN_ID)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));
        UserRoleMap roleMap = new UserRoleMap();
        roleMap.setUser(savedUser);
        roleMap.setRole(adminRole);
        roleMap.setOrg(savedOrg);
        userRoleMapRepository.save(roleMap);

        List<String> roles = List.of(Role.ADMIN);
        String accessToken  = jwtUtil.generateAccessToken(savedUser.getUserId().toString(), savedOrg.getOrgId().toString(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUserId().toString());

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getUserId().toString())
                .orgId(savedOrg.getOrgId().toString())
                .orgCode(savedOrg.getOrgCode())
                .email(request.getEmail().toLowerCase())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(roles)
                .build();
    }

    @Override
    public AuthResult login(LoginRequest request) {
        Organization org = organizationRepository.findByOrgCode(request.getOrgCode().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid organization code"));

        if (!org.getIsActive()) {
            throw new InvalidCredentialsException("Organization is deactivated");
        }

        UserDetail detail = userDetailRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        User user = detail.getUser();
        if (!user.getOrg().getOrgId().equals(org.getOrgId())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        List<String> roles = userRoleMapRepository
                .findActiveByUserIdAndOrgId(user.getUserId(), org.getOrgId())
                .stream().map(urm -> urm.getRole().getRoleName()).toList();

        String accessToken  = jwtUtil.generateAccessToken(user.getUserId().toString(), org.getOrgId().toString(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId().toString())
                .orgId(org.getOrgId().toString())
                .orgCode(org.getOrgCode())
                .email(detail.getEmail())
                .fullName(detail.getFullName())
                .gender(detail.getGender())
                .phone(detail.getPhone())
                .roles(roles)
                .build();
    }

    @Override
    public AuthTokenPair refreshTokens(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        UUID uid;
        try {
            uid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        String orgId = user.getOrg().getOrgId().toString();
        List<String> roles = userRoleMapRepository
                .findActiveByUserId(uid)
                .stream().map(urm -> urm.getRole().getRoleName()).toList();

        String newAccessToken  = jwtUtil.generateAccessToken(userId, orgId, roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        return AuthTokenPair.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
