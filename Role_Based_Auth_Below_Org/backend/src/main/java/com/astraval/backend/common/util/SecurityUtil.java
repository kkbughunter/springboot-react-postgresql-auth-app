package com.astraval.backend.common.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class SecurityUtil {

    public UUID getCurrentUserId() {
        String principal = getPrincipal();
        if (principal == null) return null;
        try {
            return UUID.fromString(principal);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UUID getCurrentOrgId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object details = auth.getDetails();
        if (details instanceof Map<?, ?> map) {
            Object orgId = map.get("orgId");
            if (orgId instanceof String s && !s.isBlank()) {
                try { return UUID.fromString(s); } catch (IllegalArgumentException ignored) {}
            }
        }
        return null;
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

    private String getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object p = auth.getPrincipal();
        return p instanceof String s ? s : null;
    }
}
