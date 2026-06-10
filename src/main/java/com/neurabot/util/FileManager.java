package com.neurabot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Handles all file I/O operations for JSON data persistence.
 * Data is stored under the user's home directory in .neurabot/data/
 */
public class FileManager {

    private static final String APP_DIR = System.getProperty("user.home") + File.separator + ".neurabot";
    private static final String DATA_DIR = APP_DIR + File.separator + "data";
    private static final String LOGS_DIR = APP_DIR + File.separator + "logs";
    private static final String REPORTS_DIR = APP_DIR + File.separator + "reports";

    public void ensureDataDirectories() {
        createDir(APP_DIR);
        createDir(DATA_DIR);
        createDir(LOGS_DIR);
        createDir(REPORTS_DIR);
    }

    private void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) System.out.println("Created directory: " + path);
        }
    }

    public String readFile(String filename) {
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) return null;
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public void writeFile(String filename, String content) {
        Path path = Paths.get(DATA_DIR, filename);
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing file " + filename + ": " + e.getMessage());
        }
    }

    public boolean writeReport(String filename, String content) {
        Path path = Paths.get(REPORTS_DIR, filename);
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
            return false;
        }
    }

    public String getReportsDir() { return REPORTS_DIR; }
    public String getDataDir() { return DATA_DIR; }
    public String getAppDir() { return APP_DIR; }

    public boolean fileExists(String filename) {
        return Files.exists(Paths.get(DATA_DIR, filename));
    }

    public boolean deleteFile(String filename) {
        try {
            return Files.deleteIfExists(Paths.get(DATA_DIR, filename));
        } catch (IOException e) {
            return false;
        }
    }
}
