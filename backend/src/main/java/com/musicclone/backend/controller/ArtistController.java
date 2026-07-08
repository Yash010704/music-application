package com.musicclone.backend.controller;

import com.musicclone.backend.dto.ArtistDto;
import com.musicclone.backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistDto>> getAll() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistDto>> search(@RequestParam String name) {
        return ResponseEntity.ok(artistService.searchArtists(name));
    }

    @PostMapping
    public ResponseEntity<ArtistDto> create(@RequestBody ArtistDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> update(@PathVariable Long id, @RequestBody ArtistDto dto) {
        return ResponseEntity.ok(artistService.updateArtist(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
