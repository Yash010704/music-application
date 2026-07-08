package com.musicclone.backend.service;

import com.musicclone.backend.dto.AlbumDto;
import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.entity.Album;
import com.musicclone.backend.entity.Artist;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAll().stream().map(this::toDto).toList();
    }

    public AlbumDto getAlbumById(Long id) {
        return toDto(findAlbumOrThrow(id));
    }

    public List<AlbumDto> getAlbumsByArtist(Long artistId) {
        return albumRepository.findByArtistId(artistId).stream().map(this::toDto).toList();
    }

    public List<AlbumDto> searchAlbums(String title) {
        return albumRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toDto).toList();
    }

    public AlbumDto createAlbum(AlbumDto dto) {
        Artist artist = artistService.findArtistOrThrow(dto.getArtistId());
        Album album = Album.builder()
                .title(dto.getTitle())
                .coverImageUrl(dto.getCoverImageUrl())
                .releaseDate(dto.getReleaseDate())
                .artist(artist)
                .build();
        return toDto(albumRepository.save(album));
    }

    public AlbumDto updateAlbum(Long id, AlbumDto dto) {
        Album album = findAlbumOrThrow(id);
        album.setTitle(dto.getTitle());
        album.setCoverImageUrl(dto.getCoverImageUrl());
        album.setReleaseDate(dto.getReleaseDate());
        if (dto.getArtistId() != null) {
            album.setArtist(artistService.findArtistOrThrow(dto.getArtistId()));
        }
        return toDto(albumRepository.save(album));
    }

    public void deleteAlbum(Long id) {
        albumRepository.delete(findAlbumOrThrow(id));
    }

    public Album findAlbumOrThrow(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));
    }

    private AlbumDto toDto(Album album) {
        List<SongDto> songDtos = album.getSongs() == null ? List.of() : album.getSongs().stream()
                .map(s -> SongDto.builder()
                        .id(s.getId())
                        .title(s.getTitle())
                        .durationSeconds(s.getDurationSeconds())
                        .coverImageUrl(s.getCoverImageUrl())
                        .genre(s.getGenre())
                        .playCount(s.getPlayCount())
                        .artistId(s.getArtist().getId())
                        .artistName(s.getArtist().getName())
                        .streamUrl("/api/stream/" + s.getId())
                        .build())
                .toList();

        return AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImageUrl(album.getCoverImageUrl())
                .releaseDate(album.getReleaseDate())
                .artistId(album.getArtist().getId())
                .artistName(album.getArtist().getName())
                .songs(songDtos)
                .build();
    }
}
