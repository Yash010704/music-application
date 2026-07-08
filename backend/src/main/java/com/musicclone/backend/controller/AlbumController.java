package com.musicclone.backend.controller;

import com.musicclone.backend.dto.AlbumDto;
import com.musicclone.backend.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAll() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumDto>> getByArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumDto>> search(@RequestParam String title) {
        return ResponseEntity.ok(albumService.searchAlbums(title));
    }

    @PostMapping
    public ResponseEntity<AlbumDto> create(@RequestBody AlbumDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDto> update(@PathVariable Long id, @RequestBody AlbumDto dto) {
        return ResponseEntity.ok(albumService.updateAlbum(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
