package app.controllers;

import app.DiaryConnection;
import app.models.DiaryEntry;
import app.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Diary editor.
 */
public class DiaryController {

    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private Button saveBtn;

    private User currentUser;
    private DiaryEntry editing; // null => new
    private Consumer<Void> onSaved;

    public void setContext(User user, DiaryEntry entry, Consumer<Void> onSaved) {
        this.currentUser = user;
        this.editing = entry;
        this.onSaved = onSaved;
        if (entry != null) {
            titleField.setText(entry.getTitle());
            contentArea.setText(entry.getContent());
        }
    }

    @FXML
    public void onSave(ActionEvent e) {
        String title = titleField.getText().trim();
        String content = contentArea.getText();
        if (title.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Title is required").showAndWait();
            return;
        }
        try {
            if (editing == null) {
                DiaryConnection.createEntry(currentUser.getId(), title, content);
            } else {
                DiaryConnection.updateEntry(editing.getId(), currentUser.getId(), title, content);
            }
            if (onSaved != null) onSaved.accept(null);
            saveBtn.getScene().getWindow().hide();
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "DB error: " + ex.getMessage()).showAndWait();
        }
    }
}
