package com.musicclone.backend.controller;

import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.security.AuthenticatedUser;
import com.musicclone.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<SongDto>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites(AuthenticatedUser.getCurrentUserId()));
    }

    @PostMapping("/{songId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long songId) {
        favoriteService.addFavorite(AuthenticatedUser.getCurrentUserId(), songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long songId) {
        favoriteService.removeFavorite(AuthenticatedUser.getCurrentUserId(), songId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{songId}/status")
    public ResponseEntity<Map<String, Boolean>> isFavorite(@PathVariable Long songId) {
        boolean fav = favoriteService.isFavorite(AuthenticatedUser.getCurrentUserId(), songId);
        return ResponseEntity.ok(Map.of("isFavorite", fav));
    }
}
