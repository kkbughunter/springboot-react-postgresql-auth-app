package com.astraval.backend.modules.auth.mapper;

import com.astraval.backend.modules.auth.response.AuthResponse;
import com.astraval.backend.modules.auth.response.AuthResult;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(AuthResult result) {
        return AuthResponse.builder()
                .userId(result.getUserId())
                .email(result.getEmail())
                .fullName(result.getFullName())
                .gender(result.getGender())
                .phone(result.getPhone())
                .roles(result.getRoles())
                .build();
    }
}
