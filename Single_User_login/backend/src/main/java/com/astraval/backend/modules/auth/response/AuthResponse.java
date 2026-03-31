package com.astraval.backend.modules.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public response body sent to the client after login / register.
 * Tokens are NOT included — they travel in httpOnly cookies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String userId;
    private String email;
    private String fullName;
    private String gender;
}
