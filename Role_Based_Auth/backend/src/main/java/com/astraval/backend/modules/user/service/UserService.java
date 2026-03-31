package com.astraval.backend.modules.user.service;

import com.astraval.backend.modules.user.request.UpdateProfileRequest;
import com.astraval.backend.modules.user.response.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse getProfile(UUID userId);

    UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);
}
