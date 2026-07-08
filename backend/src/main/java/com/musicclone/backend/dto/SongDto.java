package com.musicclone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    private Long id;
    private String title;
    private Integer durationSeconds;
    private String coverImageUrl;
    private String genre;
    private Long playCount;
    private Long artistId;
    private String artistName;
    private Long albumId;
    private String albumTitle;
    private String streamUrl;
}
