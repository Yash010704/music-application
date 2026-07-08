package com.musicclone.backend.service;

import com.musicclone.backend.dto.SongDto;
import com.musicclone.backend.entity.Favorite;
import com.musicclone.backend.entity.Song;
import com.musicclone.backend.entity.User;
import com.musicclone.backend.exception.BadRequestException;
import com.musicclone.backend.exception.ResourceNotFoundException;
import com.musicclone.backend.repository.FavoriteRepository;
import com.musicclone.backend.repository.SongRepository;
import com.musicclone.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public List<SongDto> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(fav -> toDto(fav.getSong()))
                .toList();
    }

    public void addFavorite(Long userId, Long songId) {
        if (favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new BadRequestException("Song is already in favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + songId));

        Favorite favorite = Favorite.builder().user(user).song(song).build();
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long songId) {
        favoriteRepository.deleteByUserIdAndSongId(userId, songId);
    }

    public boolean isFavorite(Long userId, Long songId) {
        return favoriteRepository.existsByUserIdAndSongId(userId, songId);
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
