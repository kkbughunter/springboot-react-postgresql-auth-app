package com.astraval.backend.modules.auth.service;

import com.astraval.backend.modules.auth.request.LoginRequest;
import com.astraval.backend.modules.auth.request.RegisterRequest;
import com.astraval.backend.modules.auth.response.AuthResult;
import com.astraval.backend.modules.auth.response.AuthTokenPair;

public interface AuthService {

    AuthResult register(RegisterRequest request);

    AuthResult login(LoginRequest request);

    AuthTokenPair refreshTokens(String refreshToken);
}
