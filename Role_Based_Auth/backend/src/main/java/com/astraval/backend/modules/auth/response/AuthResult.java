package com.astraval.backend.modules.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    private String accessToken;
    private String refreshToken;

    private String userId;
    private String orgId;
    private String orgCode;
    private String email;
    private String fullName;
    private String gender;
    private String phone;
    private List<String> roles;
}
