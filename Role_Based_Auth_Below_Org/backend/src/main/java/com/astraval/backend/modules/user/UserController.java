package com.astraval.backend.modules.user;

import com.astraval.backend.common.util.ApiResponse;
import com.astraval.backend.common.util.ApiResponseFactory;
import com.astraval.backend.common.util.SecurityUtil;
import com.astraval.backend.modules.user.request.UpdateProfileRequest;
import com.astraval.backend.modules.user.response.UserProfileResponse;
import com.astraval.backend.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    /** Get the currently authenticated user's profile. */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        UUID userId = securityUtil.getCurrentUserId();
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponseFactory.success("Profile fetched", profile));
    }

    /** Update own full name, gender, and/or phone. */
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = securityUtil.getCurrentUserId();
        UserProfileResponse updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponseFactory.success("Profile updated successfully", updated));
    }
}
