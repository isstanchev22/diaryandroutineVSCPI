package app.controllers;

import app.DiaryConnection;
import app.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;

/**
 * Handles login & registration.
 * (BG) Контролер за вход и регистрация – за пример използваме SHA-256 (за реални проекти: BCrypt).
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onLogin(ActionEvent e) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Enter username & password");
            return;
        }
        try {
            User existing = DiaryConnection.findUserByUsername(u);
            if (existing == null) {
                statusLabel.setText("User not found");
                return;
            }
            if (!existing.getPasswordHash().equals(sha256(p))) {
                statusLabel.setText("Wrong password");
                return;
            }
            gotoDashboard(existing);
        } catch (SQLException | IOException ex) {
            statusLabel.setText("DB/IO error: " + ex.getMessage());
        }
    }

    @FXML
    public void onSignup(ActionEvent e) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Enter username & password");
            return;
        }
        try {
            if (DiaryConnection.findUserByUsername(u) != null) {
                statusLabel.setText("Username taken");
                return;
            }
            User created = DiaryConnection.createUser(u, sha256(p));
            if (created != null) {
                statusLabel.setText("Account created. Logging in…");
                gotoDashboard(created);
            } else {
                statusLabel.setText("Could not create user");
            }
        } catch (SQLException | IOException ex) {
            statusLabel.setText("DB/IO error: " + ex.getMessage());
        }
    }

    private void gotoDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
        Parent root = loader.load();
        DashboardController ctrl = loader.getController();
        ctrl.setCurrentUser(user);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Diary & Routine — Dashboard");
        stage.setScene(new Scene(root, 900, 600));
    }
}
