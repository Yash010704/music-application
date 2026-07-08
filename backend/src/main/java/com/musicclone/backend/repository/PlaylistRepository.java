package com.musicclone.backend.repository;

import com.musicclone.backend.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwnerId(Long ownerId);
    List<Playlist> findByIsPublicTrue();
    List<Playlist> findByNameContainingIgnoreCaseAndIsPublicTrue(String name);
}
