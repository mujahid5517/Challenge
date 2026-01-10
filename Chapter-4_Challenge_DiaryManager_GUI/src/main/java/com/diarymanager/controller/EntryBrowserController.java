package com.diarymanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import com.diarymanager.model.DiaryEntry;
import com.diarymanager.model.DiaryManager;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EntryBrowserController {
    @FXML private ListView<DiaryEntry> entryListView;
    @FXML private TextArea previewArea;
    @FXML private Label lblTitle;
    @FXML private Label lblDate;
    @FXML private Label lblMood;
    @FXML private Label lblTags;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;
    @FXML private VBox detailsPanel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblStatus;

    private DiaryManager diaryManager;
    private ObservableList<DiaryEntry> entries;
    private DateTimeFormatter dateFormatter;

    @FXML
    public void initialize() {
        dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm");
        entries = FXCollections.observableArrayList();
        entryListView.setItems(entries);

        setupListView();
        setupButtons();
        loadEntries();
    }

    public void setDiaryManager(DiaryManager manager) {
        this.diaryManager = manager;
    }

    private void setupListView() {
        entryListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DiaryEntry entry, boolean empty) {
                super.updateItem(entry, empty);

                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellContent = new HBox(10);
                    cellContent.setPadding(new Insets(5));

                    // Mood icon
                    Label moodIcon = new Label();
                    moodIcon.setStyle("-fx-font-size: 18px;");
                    if (entry.getMood() != null && !entry.getMood().isEmpty()) {
                        String emoji = getMoodEmoji(entry.getMood());
                        moodIcon.setText(emoji);
                    }

                    // Entry info
                    VBox infoBox = new VBox(3);
                    Label titleLabel = new Label(entry.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label dateLabel = new Label(entry.getModifiedDate().format(dateFormatter));
                    dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                    Label previewLabel = new Label(entry.getPreview(60));
                    previewLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
                    previewLabel.setWrapText(true);

                    infoBox.getChildren().addAll(titleLabel, dateLabel, previewLabel);

                    cellContent.getChildren().addAll(moodIcon, infoBox);
                    setGraphic(cellContent);
                }
            }
        });

        entryListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEntryDetails(newValue));
    }

    private String getMoodEmoji(String mood) {
        if (mood.contains("Happy")) return "😊";
        if (mood.contains("Sad")) return "😢";
        if (mood.contains("Excited")) return "🤩";
        if (mood.contains("Calm")) return "😌";
        if (mood.contains("Angry")) return "😠";
        if (mood.contains("Thoughtful")) return "🤔";
        if (mood.contains("Tired")) return "😴";
        if (mood.contains("Grateful")) return "🤗";
        return "📝";
    }

    private void setupButtons() {
        btnEdit.setOnAction(e -> editSelectedEntry());
        btnDelete.setOnAction(e -> deleteSelectedEntry());
        btnRefresh.setOnAction(e -> loadEntries());

        // Disable edit/delete when no entry is selected
        btnEdit.disableProperty().bind(
                entryListView.getSelectionModel().selectedItemProperty().isNull()
        );
        btnDelete.disableProperty().bind(
                entryListView.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    private void loadEntries() {
        if (diaryManager == null) {
            diaryManager = new DiaryManager();
        }

        progressIndicator.setVisible(true);
        lblStatus.setText("Loading entries...");

        Service<List<DiaryEntry>> loadService = diaryManager.loadAllEntriesAsync();
        loadService.setOnSucceeded(event -> {
            entries.clear();
            entries.addAll(loadService.getValue());
            progressIndicator.setVisible(false);
            lblStatus.setText("Loaded " + entries.size() + " entries");

            if (!entries.isEmpty()) {
                entryListView.getSelectionModel().selectFirst();
            } else {
                clearDetails();
            }
        });

        loadService.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            lblStatus.setText("Failed to load entries");
            showAlert("Load Error", "Failed to load diary entries: " +
                    loadService.getException().getMessage());
        });

        loadService.start();
    }

    private void showEntryDetails(DiaryEntry entry) {
        if (entry == null) {
            clearDetails();
            return;
        }

        detailsPanel.setVisible(true);
        lblTitle.setText(entry.getTitle());
        lblDate.setText("Last modified: " + entry.getModifiedDate().format(dateFormatter));

        if (entry.getMood() != null && !entry.getMood().isEmpty()) {
            lblMood.setText("Mood: " + entry.getMood());
            lblMood.setVisible(true);
        } else {
            lblMood.setVisible(false);
        }

        if (entry.getTags() != null && entry.getTags().length > 0) {
            lblTags.setText("Tags: " + String.join(", ", entry.getTags()));
            lblTags.setVisible(true);
        } else {
            lblTags.setVisible(false);
        }

        // Show content preview (strip HTML tags)
        String content = entry.getContent();
        if (content != null) {
            String plainText = content.replaceAll("<[^>]*>", "");
            previewArea.setText(plainText.length() > 500 ?
                    plainText.substring(0, 500) + "..." : plainText);
        } else {
            previewArea.setText("");
        }
    }

    private void clearDetails() {
        detailsPanel.setVisible(false);
        lblTitle.setText("");
        lblDate.setText("");
        lblMood.setText("");
        lblTags.setText("");
        previewArea.setText("");
    }

    private void editSelectedEntry() {
        DiaryEntry selected = entryListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // This would open the editor window
            // In a full implementation, you'd call the main controller to open editor
            lblStatus.setText("Edit: " + selected.getTitle());
        }
    }

    private void deleteSelectedEntry() {
        DiaryEntry selected = entryListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Entry");
        confirm.setHeaderText("Delete '" + selected.getTitle() + "'?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            progressIndicator.setVisible(true);
            lblStatus.setText("Deleting entry...");

            Service<Boolean> deleteService = diaryManager.deleteEntryAsync(selected.getId());
            deleteService.setOnSucceeded(event -> {
                progressIndicator.setVisible(false);
                if (deleteService.getValue()) {
                    entries.remove(selected);
                    lblStatus.setText("Entry deleted successfully");
                    if (!entries.isEmpty()) {
                        entryListView.getSelectionModel().selectFirst();
                    } else {
                        clearDetails();
                    }
                } else {
                    lblStatus.setText("Failed to delete entry");
                }
            });

            deleteService.setOnFailed(event -> {
                progressIndicator.setVisible(false);
                lblStatus.setText("Delete failed");
                showAlert("Delete Error", "Failed to delete entry: " +
                        deleteService.getException().getMessage());
            });

            deleteService.start();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}