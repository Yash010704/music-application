package com.musicclone.backend.controller;

import com.musicclone.backend.dto.UserDto;
import com.musicclone.backend.security.AuthenticatedUser;
import com.musicclone.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getUserById(AuthenticatedUser.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UpdateProfileRequest request) {
        UserDto updated = userService.updateProfile(
                AuthenticatedUser.getCurrentUserId(),
                request.getDisplayName(),
                request.getProfileImageUrl());
        return ResponseEntity.ok(updated);
    }

    public static class UpdateProfileRequest {
        private String displayName;
        private String profileImageUrl;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}
