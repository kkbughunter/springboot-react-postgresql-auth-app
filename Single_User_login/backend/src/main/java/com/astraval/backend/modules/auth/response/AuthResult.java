package com.astraval.backend.modules.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal DTO returned by AuthService — carries both tokens (for cookie
 * setting in the controller) plus the user info that goes into the response body.
 * Never serialised directly to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    private String accessToken;
    private String refreshToken;

    private String userId;
    private String email;
    private String fullName;
    private String gender;
}
