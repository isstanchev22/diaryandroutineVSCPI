package app;

import app.models.DiaryEntry;
import app.models.Routine;
import app.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central JDBC helper for MySQL.
 **/
public class DiaryConnection {

    // 👉 Replace USER/PASS with your MySQL credentials
    private static final String URL  = "jdbc:mysql://localhost:3306/diary_app?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";         // <-- your MySQL username
    private static final String PASS = "Pancho1109!"; // <-- your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found. Add mysql-connector-j to the classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ===== Users =====
    public static User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                }
                return null;
            }
        }
    }

    public static User createUser(String username, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), username, passwordHash);
                }
            }
            return null;
        }
    }

    // ===== Diary Entries =====
    public static DiaryEntry createEntry(int userId, String title, String content) throws SQLException {
        String sql = "INSERT INTO diary_entries(user_id, title, content) VALUES(?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new DiaryEntry(keys.getInt(1), userId, title, content, null);
                }
            }
            return null;
        }
    }

    public static List<DiaryEntry> listEntries(int userId) throws SQLException {
        String sql = "SELECT id, user_id, title, content, created_at FROM diary_entries WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            List<DiaryEntry> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DiaryEntry(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toInstant()
                    ));
                }
            }
            return list;
        }
    }

    public static boolean updateEntry(int entryId, int userId, String title, String content) throws SQLException {
        String sql = "UPDATE diary_entries SET title = ?, content = ? WHERE id = ? AND user_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, entryId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deleteEntry(int entryId, int userId) throws SQLException {
        String sql = "DELETE FROM diary_entries WHERE id = ? AND user_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== Routines =====
    public static Routine createRoutine(int userId, String task) throws SQLException {
        String sql = "INSERT INTO routines(user_id, task) VALUES(?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, task);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Routine(keys.getInt(1), userId, task, false);
                }
            }
            return null;
        }
    }

    public static List<Routine> listRoutines(int userId) throws SQLException {
        String sql = "SELECT id, user_id, task, done FROM routines WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            List<Routine> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Routine(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("task"),
                            rs.getBoolean("done")
                    ));
                }
            }
            return list;
        }
    }

    public static boolean setRoutineDone(int routineId, int userId, boolean done) throws SQLException {
        String sql = "UPDATE routines SET done = ? WHERE id = ? AND user_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, done);
            ps.setInt(2, routineId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deleteRoutine(int routineId, int userId) throws SQLException {
        String sql = "DELETE FROM routines WHERE id = ? AND user_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
