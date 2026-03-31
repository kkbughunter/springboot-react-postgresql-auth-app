package com.astraval.backend.modules.user.service;

import com.astraval.backend.common.exception.UserNotFoundException;
import com.astraval.backend.modules.user.entity.User;
import com.astraval.backend.modules.user.entity.UserDetail;
import com.astraval.backend.modules.user.repo.UserDetailRepository;
import com.astraval.backend.modules.user.repo.UserRepository;
import com.astraval.backend.modules.user.request.UpdateProfileRequest;
import com.astraval.backend.modules.user.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;

    @Override
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDetail detail = userDetailRepository.findByUserId(user.getUserId()).orElse(null);
        return buildResponse(user, detail);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDetail detail = userDetailRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User details not found"));

        detail.setFullName(request.getFullName());
        // Allow explicit clear (empty string → null) or update
        if (request.getGender() != null) {
            detail.setGender(request.getGender().isBlank() ? null : request.getGender());
        }
        detail.setModifiedBy(email);
        UserDetail saved = userDetailRepository.save(detail);
        return buildResponse(user, saved);
    }

    private UserProfileResponse buildResponse(User user, UserDetail detail) {
        return UserProfileResponse.builder()
                .userId(user.getUserId().toString())
                .email(user.getEmail())
                .fullName(detail != null ? detail.getFullName() : null)
                .gender(detail != null ? detail.getGender() : null)
                .isActive(user.getIsActive())
                .build();
    }
}
