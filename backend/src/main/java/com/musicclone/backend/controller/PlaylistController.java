package com.musicclone.backend.controller;

import com.musicclone.backend.dto.PlaylistDto;
import com.musicclone.backend.dto.PlaylistRequest;
import com.musicclone.backend.security.AuthenticatedUser;
import com.musicclone.backend.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistDto>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistDto>> getMyPlaylists() {
        return ResponseEntity.ok(playlistService.getUserPlaylists(AuthenticatedUser.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getById(@PathVariable Long id) {
        Long currentUserId = tryGetCurrentUserId();
        return ResponseEntity.ok(playlistService.getPlaylistById(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<PlaylistDto> create(@Valid @RequestBody PlaylistRequest request) {
        PlaylistDto created = playlistService.createPlaylist(AuthenticatedUser.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> update(@PathVariable Long id, @Valid @RequestBody PlaylistRequest request) {
        return ResponseEntity.ok(
                playlistService.updatePlaylist(id, AuthenticatedUser.getCurrentUserId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.deletePlaylist(id, AuthenticatedUser.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<PlaylistDto> addSong(@PathVariable Long id, @PathVariable Long songId) {
        return ResponseEntity.ok(
                playlistService.addSongToPlaylist(id, AuthenticatedUser.getCurrentUserId(), songId));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<PlaylistDto> removeSong(@PathVariable Long id, @PathVariable Long songId) {
        return ResponseEntity.ok(
                playlistService.removeSongFromPlaylist(id, AuthenticatedUser.getCurrentUserId(), songId));
    }

    private Long tryGetCurrentUserId() {
        try {
            return AuthenticatedUser.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
