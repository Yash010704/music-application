package com.musicclone.backend.service;

import com.musicclone.backend.dto.ArtistDto;
import com.musicclone.backend.entity.Artist;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public List<ArtistDto> getAllArtists() {
        return artistRepository.findAll().stream().map(this::toDto).toList();
    }

    public ArtistDto getArtistById(Long id) {
        return toDto(findArtistOrThrow(id));
    }

    public List<ArtistDto> searchArtists(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).toList();
    }

    public ArtistDto createArtist(ArtistDto dto) {
        Artist artist = Artist.builder()
                .name(dto.getName())
                .bio(dto.getBio())
                .imageUrl(dto.getImageUrl())
                .build();
        return toDto(artistRepository.save(artist));
    }

    public ArtistDto updateArtist(Long id, ArtistDto dto) {
        Artist artist = findArtistOrThrow(id);
        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setImageUrl(dto.getImageUrl());
        return toDto(artistRepository.save(artist));
    }

    public void deleteArtist(Long id) {
        Artist artist = findArtistOrThrow(id);
        artistRepository.delete(artist);
    }

    public Artist findArtistOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id));
    }

    private ArtistDto toDto(Artist artist) {
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .imageUrl(artist.getImageUrl())
                .build();
    }
}
