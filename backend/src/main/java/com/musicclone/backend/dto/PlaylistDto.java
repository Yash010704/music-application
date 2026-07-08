package com.musicclone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private String description;
    private String coverImageUrl;
    private boolean isPublic;
    private Long ownerId;
    private String ownerUsername;
    private List<SongDto> songs;
}
