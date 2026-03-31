package com.astraval.backend.config;

import com.astraval.backend.common.util.JwtUtil;
import com.astraval.backend.modules.user.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        // Fallback: Authorization: Bearer <token>
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null && jwtUtil.isTokenValid(token) && jwtUtil.isAccessToken(token)) {
            String userId = jwtUtil.extractUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UUID uid = UUID.fromString(userId);
                    if (userRepository.existsById(uid)) {
                        List<String> roles = jwtUtil.extractRoles(token);
                        String orgId = jwtUtil.extractOrgId(token);

                        var authorities = roles.stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .toList();

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        authToken.setDetails(Map.of(
                                "orgId", orgId != null ? orgId : "",
                                "webDetails", new WebAuthenticationDetailsSource().buildDetails(request)
                        ));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Invalid UUID — skip authentication
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("accessToken".equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
