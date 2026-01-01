package com.diarymanager.model;

import javafx.concurrent.Task;
import javafx.concurrent.Service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DiaryManager {
    private static final String DIARY_DIR = "diary-entries";
    private static final String FILE_EXTENSION = ".diary";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public DiaryManager() {
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        Path dir = Paths.get(DIARY_DIR);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create diary directory", e);
            }
        }
    }

    public Service<Void> saveEntryAsync(DiaryEntry entry) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Saving entry...");
                        saveEntry(entry);
                        updateMessage("Entry saved successfully");
                        return null;
                    }
                };
            }
        };
    }

    public void saveEntry(DiaryEntry entry) throws IOException {
        String fileName = entry.getDateOnly() + "_" + entry.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + FILE_EXTENSION;
        Path filePath = Paths.get(DIARY_DIR, fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("TITLE:" + entry.getTitle() + "\n");
            writer.write("CREATED:" + entry.getCreatedDate().toString() + "\n");
            writer.write("MODIFIED:" + entry.getModifiedDate().toString() + "\n");
            writer.write("MOOD:" + (entry.getMood() != null ? entry.getMood() : "") + "\n");
            writer.write("TAGS:" + (entry.getTags() != null ? String.join(",", entry.getTags()) : "") + "\n");
            writer.write("CONTENT:\n");
            writer.write(entry.getContent());
        }
    }

    public Service<List<DiaryEntry>> loadAllEntriesAsync() {
        return new Service<>() {
            @Override
            protected Task<List<DiaryEntry>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<DiaryEntry> call() throws Exception {
                        updateMessage("Loading entries...");
                        List<DiaryEntry> entries = loadAllEntries();
                        updateMessage("Loaded " + entries.size() + " entries");
                        return entries;
                    }
                };
            }
        };
    }

    public List<DiaryEntry> loadAllEntries() throws IOException {
        List<DiaryEntry> entries = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIARY_DIR), "*" + FILE_EXTENSION)) {
            for (Path file : stream) {
                try {
                    DiaryEntry entry = loadEntry(file);
                    entries.add(entry);
                } catch (IOException e) {
                    System.err.println("Failed to load entry: " + file.getFileName());
                }
            }
        }

        // Sort by modified date (newest first)
        entries.sort((e1, e2) -> e2.getModifiedDate().compareTo(e1.getModifiedDate()));

        return entries;
    }

    private DiaryEntry loadEntry(Path filePath) throws IOException {
        DiaryEntry entry = new DiaryEntry();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            StringBuilder content = new StringBuilder();
            boolean inContent = false;

            while ((line = reader.readLine()) != null) {
                if (!inContent) {
                    if (line.startsWith("TITLE:")) {
                        entry.setTitle(line.substring(6));
                    } else if (line.startsWith("CREATED:")) {
                        entry.setCreatedDate(LocalDateTime.parse(line.substring(8)));
                    } else if (line.startsWith("MODIFIED:")) {
                        entry.setModifiedDate(LocalDateTime.parse(line.substring(9)));
                    } else if (line.startsWith("MOOD:")) {
                        entry.setMood(line.substring(5));
                    } else if (line.startsWith("TAGS:")) {
                        String tagsStr = line.substring(5);
                        if (!tagsStr.isEmpty()) {
                            entry.setTags(tagsStr.split(","));
                        }
                    } else if (line.equals("CONTENT:")) {
                        inContent = true;
                    }
                } else {
                    if (content.length() > 0) content.append("\n");
                    content.append(line);
                }
            }
            entry.setContent(content.toString());
        }

        return entry;
    }

    public Service<Boolean> deleteEntryAsync(String entryId) {
        return new Service<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateMessage("Deleting entry...");
                        boolean deleted = deleteEntry(entryId);
                        updateMessage(deleted ? "Entry deleted" : "Entry not found");
                        return deleted;
                    }
                };
            }
        };
    }

    public boolean deleteEntry(String entryId) throws IOException {
        List<DiaryEntry> entries = loadAllEntries();
        Optional<DiaryEntry> toDelete = entries.stream()
                .filter(e -> e.getId().equals(entryId))
                .findFirst();

        if (toDelete.isPresent()) {
            String fileName = toDelete.get().getDateOnly() + "_" +
                    toDelete.get().getTitle().replaceAll("[^a-zA-Z0-9]", "_") + FILE_EXTENSION;
            Path filePath = Paths.get(DIARY_DIR, fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        }

        return false;
    }

    public List<DiaryEntry> searchEntries(String query, String moodFilter, Date dateFilter) {
        try {
            List<DiaryEntry> allEntries = loadAllEntries();

            return allEntries.stream()
                    .filter(entry -> {
                        boolean matches = true;

                        // Text search
                        if (query != null && !query.isEmpty()) {
                            matches = entry.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                    entry.getContent().toLowerCase().contains(query.toLowerCase());
                        }

                        // Mood filter
                        if (matches && moodFilter != null && !moodFilter.isEmpty()) {
                            matches = moodFilter.equals(entry.getMood());
                        }

                        // Date filter
                        if (matches && dateFilter != null) {
                            java.time.LocalDate entryDate = entry.getModifiedDate().toLocalDate();
                            java.time.LocalDate filterDate = dateFilter.toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate();
                            matches = entryDate.equals(filterDate);
                        }

                        return matches;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Search failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}