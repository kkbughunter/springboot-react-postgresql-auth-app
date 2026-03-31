package com.astraval.backend.modules.auth;

import com.astraval.backend.common.util.ApiResponse;
import com.astraval.backend.common.util.ApiResponseFactory;
import com.astraval.backend.modules.auth.mapper.AuthMapper;
import com.astraval.backend.modules.auth.request.LoginRequest;
import com.astraval.backend.modules.auth.request.RegisterRequest;
import com.astraval.backend.modules.auth.response.AuthResponse;
import com.astraval.backend.modules.auth.response.AuthResult;
import com.astraval.backend.modules.auth.response.AuthTokenPair;
import com.astraval.backend.modules.auth.service.AuthService;
import com.astraval.backend.common.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    // ── Register ──────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        AuthResult result = authService.register(request);
        setAuthCookies(response, result.getAccessToken(), result.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFactory.success("User registered successfully", authMapper.toAuthResponse(result)));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResult result = authService.login(request);
        setAuthCookies(response, result.getAccessToken(), result.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponseFactory.success("Login successful", authMapper.toAuthResponse(result)));
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            throw new InvalidCredentialsException("Refresh token not found");
        }
        AuthTokenPair tokens = authService.refreshTokens(refreshToken);
        setAuthCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());
        return ResponseEntity.ok(ApiResponseFactory.success("Token refreshed", null));
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        clearAuthCookies(response);
        return ResponseEntity.ok(ApiResponseFactory.success("Logged out successfully", null));
    }

    // ── Cookie helpers ────────────────────────────────────────────────────────

    private void setAuthCookies(HttpServletResponse response,
                                String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie("accessToken", accessToken, Duration.ofMinutes(15)).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie("refreshToken", refreshToken, Duration.ofDays(7)).toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie("accessToken", "", Duration.ZERO).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie("refreshToken", "", Duration.ZERO).toString());
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)   // set true when deployed behind HTTPS
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }
}
