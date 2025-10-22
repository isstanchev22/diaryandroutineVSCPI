package app.models;

/**
 * Routine task.
 */
public class Routine {
    private final int id;
    private final int userId;
    private final String task;
    private boolean done;

    public Routine(int id, int userId, String task, boolean done) {
        this.id = id;
        this.userId = userId;
        this.task = task;
        this.done = done;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTask() { return task; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    @Override
    public String toString() {
        return (done ? "[✓] " : "[ ] ") + task;
    }
}
