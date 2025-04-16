package com.example.gestion_event.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String fileUrl) {
        if(StringUtils.isBlank(fileUrl)) {
            log.warn("File URL is blank or null");
            return null;
        }

        try {
            // Try to read the file from the provided path
            Path filePath = new File(fileUrl).toPath();
            log.info("Attempting to read file from: {}", filePath.toAbsolutePath());

            if (Files.exists(filePath)) {
                byte[] fileData = Files.readAllBytes(filePath);
                log.info("Successfully read file, size: {} bytes", fileData.length);
                return fileData;
            } else {
                log.warn("File does not exist at path: {}", filePath.toAbsolutePath());

                // Try to find the file in the uploads directory
                String fileName = new File(fileUrl).getName();
                Path uploadsPath = Paths.get("uploads").resolve(fileName);
                log.info("Trying alternative path: {}", uploadsPath.toAbsolutePath());

                if (Files.exists(uploadsPath)) {
                    byte[] fileData = Files.readAllBytes(uploadsPath);
                    log.info("Successfully read file from uploads directory, size: {} bytes", fileData.length);
                    return fileData;
                }
            }
        } catch (IOException e) {
            log.error("Error reading file from path: {}", fileUrl, e);
        }

        // If we couldn't read the file, return a placeholder image or null
        log.warn("Could not read file, returning null");
        return null;
    }

    public static String convertToBase64(byte[] data) {
        if (data == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }
}