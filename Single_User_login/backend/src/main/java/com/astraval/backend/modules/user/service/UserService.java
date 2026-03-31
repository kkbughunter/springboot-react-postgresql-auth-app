package com.astraval.backend.modules.user.service;

import com.astraval.backend.modules.user.request.UpdateProfileRequest;
import com.astraval.backend.modules.user.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(String email);

    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
}
