package com.musicclone.backend.service;

import com.musicclone.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file.song-upload-dir}")
    private String songUploadDir;

    @Value("${app.file.cover-upload-dir}")
    private String coverUploadDir;

    public String storeSongFile(MultipartFile file) {
        return store(file, songUploadDir);
    }

    public String storeCoverImage(MultipartFile file) {
        return store(file, coverUploadDir);
    }

    private String store(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Cannot store an empty file");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(directory).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation);

            return targetLocation.toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }
    }

    public Path resolvePath(String filePath) {
        return Paths.get(filePath).toAbsolutePath().normalize();
    }
}
