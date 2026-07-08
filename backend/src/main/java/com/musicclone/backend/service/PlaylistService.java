package com.musicclone.backend.service;

import com.musicclone.backend.dto.PlaylistDto;
import com.musicclone.backend.dto.PlaylistRequest;
import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.entity.Playlist;
import com.musicclone.backend.entity.Song;
import com.musicclone.backend.entity.User;
import com.musicclone.backend.exception.BadRequestException;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.PlaylistRepository;
import com.musicclone.backend.repository.SongRepository;
import com.musicclone.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public List<PlaylistDto> getPublicPlaylists() {
        return playlistRepository.findByIsPublicTrue().stream().map(this::toDto).toList();
    }

    public List<PlaylistDto> getUserPlaylists(Long userId) {
        return playlistRepository.findByOwnerId(userId).stream().map(this::toDto).toList();
    }

    public PlaylistDto getPlaylistById(Long id, Long requestingUserId) {
        Playlist playlist = findPlaylistOrThrow(id);
        if (!playlist.isPublic() && !playlist.getOwner().getId().equals(requestingUserId)) {
            throw new BadRequestException("This playlist is private");
        }
        return toDto(playlist);
    }

    public PlaylistDto createPlaylist(Long ownerId, PlaylistRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .isPublic(request.isPublic())
                .owner(owner)
                .songs(new LinkedHashSet<>())
                .build();

        return toDto(playlistRepository.save(playlist));
    }

    public PlaylistDto updatePlaylist(Long id, Long ownerId, PlaylistRequest request) {
        Playlist playlist = findPlaylistOrThrow(id);
        assertOwnership(playlist, ownerId);

        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setCoverImageUrl(request.getCoverImageUrl());
        playlist.setPublic(request.isPublic());

        return toDto(playlistRepository.save(playlist));
    }

    public void deletePlaylist(Long id, Long ownerId) {
        Playlist playlist = findPlaylistOrThrow(id);
        assertOwnership(playlist, ownerId);
        playlistRepository.delete(playlist);
    }

    public PlaylistDto addSongToPlaylist(Long playlistId, Long ownerId, Long songId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        assertOwnership(playlist, ownerId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + songId));

        playlist.getSongs().add(song);
        return toDto(playlistRepository.save(playlist));
    }

    public PlaylistDto removeSongFromPlaylist(Long playlistId, Long ownerId, Long songId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        assertOwnership(playlist, ownerId);

        playlist.getSongs().removeIf(s -> s.getId().equals(songId));
        return toDto(playlistRepository.save(playlist));
    }

    private void assertOwnership(Playlist playlist, Long userId) {
        if (!playlist.getOwner().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to modify this playlist");
        }
    }

    private Playlist findPlaylistOrThrow(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));
    }

    private PlaylistDto toDto(Playlist playlist) {
        List<SongDto> songDtos = playlist.getSongs().stream()
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

        return PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .coverImageUrl(playlist.getCoverImageUrl())
                .isPublic(playlist.isPublic())
                .ownerId(playlist.getOwner().getId())
                .ownerUsername(playlist.getOwner().getUsername())
                .songs(songDtos)
                .build();
    }
}
