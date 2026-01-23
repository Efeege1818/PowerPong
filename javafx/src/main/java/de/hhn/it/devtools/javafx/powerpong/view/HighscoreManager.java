package de.hhn.it.devtools.javafx.powerpong.view;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages persistent highscores for PowerPong Survival mode.
 * Stores scores in a JSON file in user's home directory.
 */
public class HighscoreManager {
    private static final String HIGHSCORE_FILE = System.getProperty("user.home") +
            File.separator + ".powerpong_highscores.json";
    private static final int MAX_ENTRIES = 10;

    private List<HighscoreEntry> entries = new ArrayList<>();

    /**
     * A single highscore entry.
     */
    public record HighscoreEntry(String name, int score, long timestamp) implements Comparable<HighscoreEntry> {
        @Override
        public int compareTo(HighscoreEntry other) {
            return Integer.compare(other.score, this.score); // Descending order
        }
    }

    public HighscoreManager() {
        load();
    }

    /**
     * Adds a new score to the leaderboard if it qualifies.
     * 
     * @param name  Player name
     * @param score The score achieved
     * @return true if score made it to the leaderboard
     */
    public boolean addScore(String name, int score) {
        HighscoreEntry entry = new HighscoreEntry(name, score, System.currentTimeMillis());
        entries.add(entry);
        Collections.sort(entries);

        // Keep only top N entries
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }

        save();
        return entries.contains(entry);
    }

    /**
     * Checks if a score would qualify for the leaderboard.
     * 
     * @param score The score to check
     * @return true if score would make the top 10
     */
    public boolean isHighscore(int score) {
        if (entries.size() < MAX_ENTRIES)
            return true;
        return score > entries.get(entries.size() - 1).score;
    }

    /**
     * Gets all highscore entries.
     * 
     * @return Unmodifiable list of entries, sorted by score descending
     */
    public List<HighscoreEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Gets the top score, or 0 if no scores exist.
     * 
     * @return The highest score
     */
    public int getTopScore() {
        return entries.isEmpty() ? 0 : entries.get(0).score;
    }

    private void load() {
        try {
            Path path = Paths.get(HIGHSCORE_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                parseJson(content);
            }
        } catch (Exception e) {
            System.err.println("Could not load highscores: " + e.getMessage());
            entries = new ArrayList<>();
        }
    }

    private void save() {
        try {
            String json = toJson();
            Files.writeString(Paths.get(HIGHSCORE_FILE), json);
        } catch (Exception e) {
            System.err.println("Could not save highscores: " + e.getMessage());
        }
    }

    // Simple JSON serialization (avoid external dependencies)
    private String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < entries.size(); i++) {
            HighscoreEntry e = entries.get(i);
            sb.append("  {\"name\":\"").append(escapeJson(e.name))
                    .append("\",\"score\":").append(e.score)
                    .append(",\"timestamp\":").append(e.timestamp).append("}");
            if (i < entries.size() - 1)
                sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private void parseJson(String json) {
        entries = new ArrayList<>();
        // Simple regex-based parsing for our known format
        String pattern = "\"name\":\"([^\"]*)\",\"score\":(\\d+),\"timestamp\":(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);

        while (m.find()) {
            String name = m.group(1);
            int score = Integer.parseInt(m.group(2));
            long timestamp = Long.parseLong(m.group(3));
            entries.add(new HighscoreEntry(name, score, timestamp));
        }
        Collections.sort(entries);
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
