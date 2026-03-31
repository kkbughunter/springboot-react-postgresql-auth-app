package com.astraval.backend.modules.auth.mapper;

import com.astraval.backend.modules.auth.response.AuthResponse;
import com.astraval.backend.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user, String fullName, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId().toString())
                .email(user.getEmail())
                .fullName(fullName)
                .build();
    }
}
