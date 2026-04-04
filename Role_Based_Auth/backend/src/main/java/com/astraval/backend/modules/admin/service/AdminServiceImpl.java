package com.astraval.backend.modules.admin.service;

import com.astraval.backend.common.exception.UserAlreadyExistsException;
import com.astraval.backend.common.exception.UserNotFoundException;
import com.astraval.backend.modules.admin.request.AdminUpdateUserRequest;
import com.astraval.backend.modules.admin.request.CreateUserRequest;
import com.astraval.backend.modules.admin.response.AdminUserResponse;
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
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminUserResponse createUser(UUID adminUserId, CreateUserRequest request) {
        if (userDetailRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        User newUser = new User();
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(newUser);

        UserDetail detail = new UserDetail();
        detail.setUser(savedUser);
        detail.setFullName(request.getFullName());
        detail.setEmail(request.getEmail().toLowerCase());
        detail.setGender(request.getGender());
        detail.setPhone(request.getPhone());
        detail.setCreatedBy(adminUserId.toString());
        detail.setModifiedBy(adminUserId.toString());
        userDetailRepository.save(detail);

        int roleId = request.getRoleId() != null ? request.getRoleId() : Role.USER_ID;
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role not found with id: " + roleId));

        UserRoleMap roleMap = new UserRoleMap();
        roleMap.setUser(savedUser);
        roleMap.setRole(role);
        roleMap.setCreatedBy(adminUserId.toString());
        roleMap.setModifiedBy(adminUserId.toString());
        userRoleMapRepository.save(roleMap);

        return buildResponse(savedUser, detail, List.of(role.getRoleName()));
    }

    @Override
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAllActive().stream().map(user -> {
            UserDetail detail = userDetailRepository.findByUserId(user.getUserId()).orElse(null);
            List<String> roles = userRoleMapRepository
                    .findActiveByUserId(user.getUserId())
                    .stream().map(urm -> urm.getRole().getRoleName()).toList();
            return buildResponse(user, detail, roles);
        }).toList();
    }

    @Override
    public AdminUserResponse getUser(UUID targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDetail detail = userDetailRepository.findByUserId(targetUserId).orElse(null);
        List<String> roles = userRoleMapRepository
                .findActiveByUserId(targetUserId)
                .stream().map(urm -> urm.getRole().getRoleName()).toList();
        return buildResponse(user, detail, roles);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(UUID targetUserId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDetail detail = userDetailRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User details not found"));

        if (request.getFullName() != null) {
            detail.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            detail.setGender(request.getGender().isBlank() ? null : request.getGender());
        }
        if (request.getPhone() != null) {
            detail.setPhone(request.getPhone().isBlank() ? null : request.getPhone());
        }
        userDetailRepository.save(detail);

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
            detail.setIsActive(request.getIsActive());
            userRepository.save(user);
        }

        List<String> roles = userRoleMapRepository
                .findActiveByUserId(targetUserId)
                .stream().map(urm -> urm.getRole().getRoleName()).toList();
        return buildResponse(user, detail, roles);
    }

    @Override
    @Transactional
    public void deleteUser(UUID targetUserId, UUID adminUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDetail detail = userDetailRepository.findByUserId(targetUserId).orElse(null);

        user.setIsActive(false);
        user.setModifiedBy(adminUserId.toString());
        userRepository.save(user);

        if (detail != null) {
            detail.setIsActive(false);
            detail.setModifiedBy(adminUserId.toString());
            userDetailRepository.save(detail);
        }
    }

    private AdminUserResponse buildResponse(User user, UserDetail detail, List<String> roles) {
        return AdminUserResponse.builder()
                .userId(user.getUserId().toString())
                .email(detail != null ? detail.getEmail() : null)
                .fullName(detail != null ? detail.getFullName() : null)
                .gender(detail != null ? detail.getGender() : null)
                .phone(detail != null ? detail.getPhone() : null)
                .isActive(user.getIsActive())
                .roles(roles)
                .build();
    }
}
