package com.musicclone.backend.service;

import com.musicclone.backend.dto.UserDto;
import com.musicclone.backend.entity.User;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserById(Long id) {
        return toDto(findUserOrThrow(id));
    }

    public UserDto updateProfile(Long id, String displayName, String profileImageUrl) {
        User user = findUserOrThrow(id);
        if (displayName != null) {
            user.setDisplayName(displayName);
        }
        if (profileImageUrl != null) {
            user.setProfileImageUrl(profileImageUrl);
        }
        return toDto(userRepository.save(user));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .build();
    }
}
