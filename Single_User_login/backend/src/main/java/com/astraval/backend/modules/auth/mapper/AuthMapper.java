package com.astraval.backend.modules.auth.mapper;

import com.astraval.backend.modules.auth.response.AuthResponse;
import com.astraval.backend.modules.auth.response.AuthResult;
import com.astraval.backend.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResult toAuthResult(User user, String fullName, String gender,
                                   String accessToken, String refreshToken) {
        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId().toString())
                .email(user.getEmail())
                .fullName(fullName)
                .gender(gender)
                .build();
    }

    public AuthResponse toAuthResponse(AuthResult result) {
        return AuthResponse.builder()
                .userId(result.getUserId())
                .email(result.getEmail())
                .fullName(result.getFullName())
                .gender(result.getGender())
                .build();
    }
}
