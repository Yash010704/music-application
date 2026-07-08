package com.musicclone.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new IllegalStateException("No authenticated user found in security context");
    }
}
