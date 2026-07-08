package com.musicclone.backend.controller;

import com.musicclone.backend.entity.Song;
import com.musicclone.backend.service.FileStorageService;
import com.musicclone.backend.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 * Streams song audio files with support for HTTP Range requests so browsers/audio
 * players can seek within a track without downloading the whole file first.
 */
@RestController
@RequiredArgsConstructor
public class StreamController {

    private final SongService songService;
    private final FileStorageService fileStorageService;

    @GetMapping("/api/stream/{songId}")
    public ResponseEntity<org.springframework.core.io.Resource> stream(
            @PathVariable Long songId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        Song song = songService.findSongOrThrow(songId);
        Path path = fileStorageService.resolvePath(song.getFilePath());
        org.springframework.core.io.Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        long fileLength = resource.contentLength();
        MediaType mediaType = MediaType.parseMediaType(guessAudioContentType(path.toString()));

        if (rangeHeader == null) {
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .contentLength(fileLength)
                    .body(resource);
        }

        // Parse "bytes=start-end"
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty()
                ? Long.parseLong(ranges[1])
                : fileLength - 1;
        if (rangeEnd > fileLength - 1) {
            rangeEnd = fileLength - 1;
        }
        long contentLength = rangeEnd - rangeStart + 1;

        InputStreamResourceWithRange rangeResource = new InputStreamResourceWithRange(path, rangeStart, contentLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(mediaType)
                .contentLength(contentLength)
                .body(rangeResource);
    }

    private String guessAudioContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".wav")) return "audio/wav";
        if (lower.endsWith(".ogg")) return "audio/ogg";
        if (lower.endsWith(".flac")) return "audio/flac";
        if (lower.endsWith(".m4a")) return "audio/mp4";
        return "application/octet-stream";
    }

    /**
     * A simple Resource implementation that reads only the requested byte range
     * from disk, used to serve HTTP 206 Partial Content responses.
     */
    static class InputStreamResourceWithRange extends org.springframework.core.io.AbstractResource {
        private final Path path;
        private final long start;
        private final long length;

        InputStreamResourceWithRange(Path path, long start, long length) {
            this.path = path;
            this.start = start;
            this.length = length;
        }

        @Override
        public String getDescription() {
            return "Range of file [" + path + "]";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r");
            raf.seek(start);
            byte[] data = new byte[(int) length];
            raf.readFully(data);
            raf.close();
            return new java.io.ByteArrayInputStream(data);
        }

        @Override
        public long contentLength() {
            return length;
        }
    }
}
