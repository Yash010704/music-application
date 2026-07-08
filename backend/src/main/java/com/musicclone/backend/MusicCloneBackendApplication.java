package com.musicclone.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MusicCloneBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicCloneBackendApplication.class, args);
    }
}
