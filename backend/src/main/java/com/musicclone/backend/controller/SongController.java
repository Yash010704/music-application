package com.musicclone.backend.controller;

import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(songService.search(query));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<SongDto>> getTrending() {
        return ResponseEntity.ok(songService.getTrending());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<SongDto>> getByArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<SongDto>> getByAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    // Restricted to authenticated users (artists/admins) - upload a new song with its audio file
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SongDto> uploadSong(
            @RequestParam String title,
            @RequestParam(required = false) Integer durationSeconds,
            @RequestParam(required = false) String genre,
            @RequestParam Long artistId,
            @RequestParam(required = false) Long albumId,
            @RequestParam("songFile") MultipartFile songFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

        SongDto created = songService.uploadSong(title, durationSeconds, genre, artistId, albumId, songFile, coverImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<SongDto> registerPlay(@PathVariable Long id) {
        return ResponseEntity.ok(songService.incrementPlayCount(id));
    }
}
