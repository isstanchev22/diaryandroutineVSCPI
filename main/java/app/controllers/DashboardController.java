package app.controllers;

import app.DiaryConnection;
import app.models.DiaryEntry;
import app.models.Routine;
import app.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main dashboard for diary + routines.
 * (BG) Табло: списъци с дневник и рутини + бързи действия.
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<DiaryEntry> diaryList;
    @FXML private ListView<Routine> routineList;
    @FXML private TextField newRoutineField;

    private User currentUser;
    private final ObservableList<DiaryEntry> diaryData = FXCollections.observableArrayList();
    private final ObservableList<Routine> routineData = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername());
        refreshData();
    }

    @FXML
    public void refreshData() {
        try {
            diaryData.setAll(DiaryConnection.listEntries(currentUser.getId()));
            routineData.setAll(DiaryConnection.listRoutines(currentUser.getId()));
            diaryList.setItems(diaryData);
            routineList.setItems(routineData);
        } catch (SQLException e) {
            showError("DB error: " + e.getMessage());
        }
    }

    @FXML
    public void onAddRoutine(ActionEvent e) {
        String task = newRoutineField.getText().trim();
        if (task.isEmpty()) return;
        try {
            Routine r = DiaryConnection.createRoutine(currentUser.getId(), task);
            if (r != null) {
                routineData.add(0, r);
                newRoutineField.clear();
            }
        } catch (SQLException ex) {
            showError("DB error: " + ex.getMessage());
        }
    }

    @FXML
    public void onToggleRoutine(ActionEvent e) {
        Routine selected = routineList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            boolean newState = !selected.isDone();
            if (DiaryConnection.setRoutineDone(selected.getId(), currentUser.getId(), newState)) {
                selected.setDone(newState);
                routineList.refresh();
            }
        } catch (SQLException ex) {
            showError("DB error: " + ex.getMessage());
        }
    }

    @FXML
    public void onDeleteRoutine(ActionEvent e) {
        Routine selected = routineList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            if (DiaryConnection.deleteRoutine(selected.getId(), currentUser.getId())) {
                routineData.remove(selected);
            }
        } catch (SQLException ex) {
            showError("DB error: " + ex.getMessage());
        }
    }

    @FXML
    public void onNewEntry(ActionEvent e) throws IOException {
        openDiaryEditor(null);
    }

    @FXML
    public void onEditEntry(ActionEvent e) throws IOException {
        DiaryEntry selected = diaryList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDiaryEditor(selected);
        }
    }

    @FXML
    public void onDeleteEntry(ActionEvent e) {
        DiaryEntry selected = diaryList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            if (DiaryConnection.deleteEntry(selected.getId(), currentUser.getId())) {
                diaryData.remove(selected);
            }
        } catch (SQLException ex) {
            showError("DB error: " + ex.getMessage());
        }
    }

    private void openDiaryEditor(DiaryEntry entry) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/diary.fxml"));
        Parent root = loader.load();
        DiaryController ctrl = loader.getController();

        // ✅ FIX: use lambda to ignore parameters from callback
        ctrl.setContext(currentUser, entry, v -> refreshData());

        Stage stage = new Stage();
        stage.setTitle(entry == null ? "New Entry" : "Edit Entry");
        stage.setScene(new Scene(root, 640, 520));
        stage.show();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
