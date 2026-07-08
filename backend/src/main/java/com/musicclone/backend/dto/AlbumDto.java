package com.musicclone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {
    private Long id;
    private String title;
    private String coverImageUrl;
    private LocalDate releaseDate;
    private Long artistId;
    private String artistName;
    private List<SongDto> songs;
}
