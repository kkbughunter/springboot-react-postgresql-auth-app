package com.astraval.backend.modules.user.service;

import com.astraval.backend.common.exception.UserNotFoundException;
import com.astraval.backend.modules.user.entity.User;
import com.astraval.backend.modules.user.entity.UserDetail;
import com.astraval.backend.modules.user.repo.UserDetailRepository;
import com.astraval.backend.modules.user.repo.UserRepository;
import com.astraval.backend.modules.user.repo.UserRoleMapRepository;
import com.astraval.backend.modules.user.request.UpdateProfileRequest;
import com.astraval.backend.modules.user.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserRoleMapRepository userRoleMapRepository;

    @Override
    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDetail detail = userDetailRepository.findByUserId(userId).orElse(null);
        List<String> roles = userRoleMapRepository
                .findActiveByUserId(userId)
                .stream().map(urm -> urm.getRole().getRoleName()).toList();
        return buildResponse(user, detail, roles);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDetail detail = userDetailRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User details not found"));

        detail.setFullName(request.getFullName());
        if (request.getGender() != null) {
            detail.setGender(request.getGender().isBlank() ? null : request.getGender());
        }
        if (request.getPhone() != null) {
            detail.setPhone(request.getPhone().isBlank() ? null : request.getPhone());
        }
        detail.setModifiedBy(userId.toString());
        UserDetail saved = userDetailRepository.save(detail);

        List<String> roles = userRoleMapRepository
                .findActiveByUserId(userId)
                .stream().map(urm -> urm.getRole().getRoleName()).toList();
        return buildResponse(user, saved, roles);
    }

    private UserProfileResponse buildResponse(User user, UserDetail detail, List<String> roles) {
        return UserProfileResponse.builder()
                .userId(user.getUserId().toString())
                .orgId(user.getOrg().getOrgId().toString())
                .orgCode(user.getOrg().getOrgCode())
                .email(detail != null ? detail.getEmail() : null)
                .fullName(detail != null ? detail.getFullName() : null)
                .gender(detail != null ? detail.getGender() : null)
                .phone(detail != null ? detail.getPhone() : null)
                .isActive(user.getIsActive())
                .roles(roles)
                .build();
    }
}
