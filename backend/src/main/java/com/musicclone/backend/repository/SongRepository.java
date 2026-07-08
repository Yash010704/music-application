package com.musicclone.backend.repository;

import com.musicclone.backend.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByArtistId(Long artistId);

    List<Song> findByAlbumId(Long albumId);

    List<Song> findByTitleContainingIgnoreCase(String title);

    List<Song> findByGenreIgnoreCase(String genre);

    @Query("SELECT s FROM Song s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.artist.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchByTitleOrArtist(@Param("query") String query);

    List<Song> findTop20ByOrderByPlayCountDesc();
}
