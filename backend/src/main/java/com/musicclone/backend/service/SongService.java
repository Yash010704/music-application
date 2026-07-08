package com.musicclone.backend.service;

import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.entity.Album;
import com.musicclone.backend.entity.Artist;
import com.musicclone.backend.entity.Song;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final FileStorageService fileStorageService;

    public List<SongDto> getAllSongs() {
        return songRepository.findAll().stream().map(this::toDto).toList();
    }

    public SongDto getSongById(Long id) {
        return toDto(findSongOrThrow(id));
    }

    public List<SongDto> getSongsByArtist(Long artistId) {
        return songRepository.findByArtistId(artistId).stream().map(this::toDto).toList();
    }

    public List<SongDto> getSongsByAlbum(Long albumId) {
        return songRepository.findByAlbumId(albumId).stream().map(this::toDto).toList();
    }

    public List<SongDto> search(String query) {
        return songRepository.searchByTitleOrArtist(query).stream().map(this::toDto).toList();
    }

    public List<SongDto> getTrending() {
        return songRepository.findTop20ByOrderByPlayCountDesc().stream().map(this::toDto).toList();
    }

    public SongDto uploadSong(String title, Integer durationSeconds, String genre,
                               Long artistId, Long albumId,
                               MultipartFile songFile, MultipartFile coverImage) {

        Artist artist = artistService.findArtistOrThrow(artistId);
        Album album = albumId != null ? albumService.findAlbumOrThrow(albumId) : null;

        String storedSongPath = fileStorageService.storeSongFile(songFile);
        String coverUrl = coverImage != null && !coverImage.isEmpty()
                ? fileStorageService.storeCoverImage(coverImage)
                : null;

        Song song = Song.builder()
                .title(title)
                .durationSeconds(durationSeconds)
                .genre(genre)
                .filePath(storedSongPath)
                .coverImageUrl(coverUrl)
                .artist(artist)
                .album(album)
                .playCount(0L)
                .build();

        return toDto(songRepository.save(song));
    }

    public void deleteSong(Long id) {
        songRepository.delete(findSongOrThrow(id));
    }

    public SongDto incrementPlayCount(Long id) {
        Song song = findSongOrThrow(id);
        song.setPlayCount(song.getPlayCount() + 1);
        return toDto(songRepository.save(song));
    }

    public Song findSongOrThrow(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + id));
    }

    private SongDto toDto(Song song) {
        return SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .durationSeconds(song.getDurationSeconds())
                .coverImageUrl(song.getCoverImageUrl())
                .genre(song.getGenre())
                .playCount(song.getPlayCount())
                .artistId(song.getArtist().getId())
                .artistName(song.getArtist().getName())
                .albumId(song.getAlbum() != null ? song.getAlbum().getId() : null)
                .albumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .streamUrl("/api/stream/" + song.getId())
                .build();
    }
}
