package com.musicclone.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistRequest {

    @NotBlank(message = "Playlist name is required")
    private String name;

    private String description;
    private String coverImageUrl;
    private boolean isPublic = true;
}
