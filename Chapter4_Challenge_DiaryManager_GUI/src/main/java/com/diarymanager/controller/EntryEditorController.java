package com.diarymanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import com.diarymanager.model.DiaryEntry;
import com.diarymanager.model.DiaryManager;
import javafx.concurrent.Service;
import java.time.LocalDateTime;

public class EntryEditorController {
    @FXML private TextField txtTitle;
    @FXML private HTMLEditor htmlEditor;
    @FXML private ComboBox<String> cmbMood;
    @FXML private TextField txtTags;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private HBox toolbar;
    @FXML private Label lblStatus;
    @FXML private ProgressIndicator progressIndicator;

    private DiaryManager diaryManager;
    private DiaryEntry currentEntry;
    private boolean saved = false;
    private Service<?> autoSaveService;

    @FXML
    public void initialize() {
        diaryManager = new DiaryManager();

        // Setup mood combobox
        cmbMood.getItems().addAll("", "😊 Happy", "😢 Sad", "🤩 Excited", "😌 Calm",
                "😠 Angry", "🤔 Thoughtful", "😴 Tired", "🤗 Grateful");

        // Setup auto-save
        setupAutoSave();

        // Setup toolbar buttons
        setupToolbar();

        // Set focus to title field
        txtTitle.requestFocus();
    }

    public void setEntryForEditing(DiaryEntry entry) {
        this.currentEntry = entry;
        txtTitle.setText(entry.getTitle());
        htmlEditor.setHtmlText(entry.getContent());
        if (entry.getMood() != null && !entry.getMood().isEmpty()) {
            cmbMood.setValue(entry.getMood());
        }
        if (entry.getTags() != null) {
            txtTags.setText(String.join(", ", entry.getTags()));
        }
    }

    private void setupAutoSave() {
        // Auto-save on text change (with debounce)
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            scheduleAutoSave();
        });

        htmlEditor.setOnKeyReleased(event -> {
            scheduleAutoSave();
        });
    }

    private void scheduleAutoSave() {
        if (autoSaveService != null && autoSaveService.isRunning()) {
            autoSaveService.cancel();
        }

        autoSaveService = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000); // Wait 2 seconds before auto-saving
                        saveEntry(false);
                        return null;
                    }
                };
            }
        };
        autoSaveService.start();
    }

    private void setupToolbar() {
        // Add formatting buttons to toolbar
        Button btnBold = createToolbarButton("B", "Bold");
        btnBold.setOnAction(e -> formatText("bold"));

        Button btnItalic = createToolbarButton("I", "Italic");
        btnItalic.setOnAction(e -> formatText("italic"));

        Button btnUnderline = createToolbarButton("U", "Underline");
        btnUnderline.setOnAction(e -> formatText("underline"));

        toolbar.getChildren().addAll(btnBold, btnItalic, btnUnderline);
    }

    private Button createToolbarButton(String text, String tooltip) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().add("toolbar-button");
        return button;
    }

    private void formatText(String format) {
        // This is a simplified version - in a real app, you'd use JavaScript
        // to manipulate the HTML editor's content
        lblStatus.setText("Format: " + format + " (Note: Full formatting requires JavaScript integration)");
    }

    @FXML
    private void handleSave() {
        saveEntry(true);
    }

    private void saveEntry(boolean showConfirmation) {
        if (txtTitle.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a title for your entry.");
            return;
        }

        DiaryEntry entry = currentEntry != null ? currentEntry : new DiaryEntry();
        entry.setTitle(txtTitle.getText().trim());
        entry.setContent(htmlEditor.getHtmlText());
        entry.setModifiedDate(LocalDateTime.now());

        if (cmbMood.getValue() != null && !cmbMood.getValue().isEmpty()) {
            entry.setMood(cmbMood.getValue());
        }

        if (!txtTags.getText().trim().isEmpty()) {
            entry.setTags(txtTags.getText().split(",\\s*"));
        }

        progressIndicator.setVisible(true);
        lblStatus.setText("Saving...");

        Service<Void> saveService = diaryManager.saveEntryAsync(entry);
        saveService.setOnSucceeded(event -> {
            progressIndicator.setVisible(false);
            saved = true;
            lblStatus.setText("Saved successfully!");

            if (showConfirmation) {
                closeWindow();
            }
        });

        saveService.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            lblStatus.setText("Save failed!");
            showAlert("Save Error", "Failed to save entry: " +
                    saveService.getException().getMessage());
        });

        saveService.start();
    }

    @FXML
    private void handleCancel() {
        if (!saved && hasChanges()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Unsaved Changes");
            confirm.setHeaderText("You have unsaved changes");
            confirm.setContentText("Do you want to discard changes?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                closeWindow();
            }
        } else {
            closeWindow();
        }
    }

    private boolean hasChanges() {
        if (currentEntry == null) {
            return !txtTitle.getText().isEmpty() ||
                    !htmlEditor.getHtmlText().equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>");
        }

        return !txtTitle.getText().equals(currentEntry.getTitle()) ||
                !htmlEditor.getHtmlText().equals(currentEntry.getContent());
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSaved() {
        return saved;
    }
}