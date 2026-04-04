package com.astraval.backend.modules.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pair of tokens returned by the refresh-token flow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenPair {
    private String accessToken;
    private String refreshToken;
}
