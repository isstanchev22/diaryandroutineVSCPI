package app.models;

import java.time.Instant;

/**
 * Diary entry record.
 **/
public class DiaryEntry {
    private final int id;
    private final int userId;
    private final String title;
    private final String content;
    private final Instant createdAt; // may be null for brand-new

    public DiaryEntry(int id, int userId, String title, String content, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return createdAt == null ? title : title + " (" + createdAt + ")";
    }
}
