package com.diarymanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.diarymanager.model.DiaryEntry;
import com.diarymanager.model.DiaryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.time.LocalDate;

public class MainController {
    @FXML private BorderPane mainContainer;
    @FXML private Button btnNewEntry;
    @FXML private Button btnBrowseEntries;
    @FXML private Button btnSearch;
    @FXML private Button btnSettings;
    @FXML private ToggleButton btnThemeToggle;
    @FXML private VBox navigationPanel;
    @FXML private Label lblStatus;

    private DiaryManager diaryManager;
    private boolean darkMode = false;

    @FXML
    public void initialize() {
        diaryManager = new DiaryManager();
        setupNavigation();
    }

    private void setupNavigation() {
        // Set up button actions
        btnNewEntry.setOnAction(e -> openEditor(null));
        btnBrowseEntries.setOnAction(e -> openBrowser());
        btnSearch.setOnAction(e -> openSearch());
        btnSettings.setOnAction(e -> openSettings());
        btnThemeToggle.setOnAction(e -> toggleTheme());

        // Add keyboard shortcuts
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        mainContainer.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case N:
                    if (event.isControlDown()) openEditor(null);
                    break;
                case B:
                    if (event.isControlDown()) openBrowser();
                    break;
                case F:
                    if (event.isControlDown()) openSearch();
                    break;
                case T:
                    if (event.isControlDown() && event.isShiftDown()) toggleTheme();
                    break;
            }
        });
    }

    private void openEditor(DiaryEntry entry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/diarymanager/views/editor-view.fxml"));
            Parent editorRoot = loader.load();

            EntryEditorController controller = loader.getController();
            if (entry != null) {
                controller.setEntryForEditing(entry);
            }

            Stage editorStage = new Stage();
            editorStage.initModality(Modality.WINDOW_MODAL);
            editorStage.initOwner(mainContainer.getScene().getWindow());
            editorStage.setTitle(entry == null ? "New Diary Entry" : "Edit: " + entry.getTitle());

            Scene scene = new Scene(editorRoot, 900, 700);
            if (darkMode) {
                scene.getStylesheets().add(getClass().getResource("/com/diarymanager/css/styles-dark.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/com/diarymanager/css/styles-light.css").toExternalForm());
            }

            editorStage.setScene(scene);
            editorStage.showAndWait();

            // Refresh if needed
            if (controller.isSaved()) {
                updateStatus("Entry saved successfully");
            }

        } catch (IOException e) {
            showError("Failed to open editor", e.getMessage());
        }
    }

    private void openBrowser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/diarymanager/views/browser-view.fxml"));
            Parent browserRoot = loader.load();

            EntryBrowserController controller = loader.getController();
            controller.setDiaryManager(diaryManager);

            // Replace center content
            mainContainer.setCenter(browserRoot);
            updateStatus("Browse mode");

        } catch (IOException e) {
            showError("Failed to open browser", e.getMessage());
        }
    }

    private void openSearch() {
        try {
            // Create search dialog
            Dialog<DiaryEntry> searchDialog = new Dialog<>();
            searchDialog.setTitle("Search Diary Entries");
            searchDialog.setHeaderText("Find entries by content, mood, or date");

            // Set dialog buttons
            ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
            searchDialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

            // Create search form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField txtSearch = new TextField();
            txtSearch.setPromptText("Search term");

            ComboBox<String> cmbMood = new ComboBox<>();
            cmbMood.setPromptText("Select mood");
            cmbMood.getItems().addAll("Happy", "Sad", "Excited", "Calm", "Angry", "Thoughtful");

            DatePicker datePicker = new DatePicker();
            datePicker.setPromptText("Select date");

            grid.add(new Label("Search:"), 0, 0);
            grid.add(txtSearch, 1, 0);
            grid.add(new Label("Mood:"), 0, 1);
            grid.add(cmbMood, 1, 1);
            grid.add(new Label("Date:"), 0, 2);
            grid.add(datePicker, 1, 2);

            searchDialog.getDialogPane().setContent(grid);

            // Enable/Disable search button based on input
            Node searchButton = searchDialog.getDialogPane().lookupButton(searchButtonType);
            searchButton.setDisable(true);

            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                searchButton.setDisable(newValue.trim().isEmpty() &&
                        cmbMood.getValue() == null &&
                        datePicker.getValue() == null);
            });

            cmbMood.valueProperty().addListener((observable, oldValue, newValue) -> {
                searchButton.setDisable(txtSearch.getText().trim().isEmpty() &&
                        newValue == null &&
                        datePicker.getValue() == null);
            });

            datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                searchButton.setDisable(txtSearch.getText().trim().isEmpty() &&
                        cmbMood.getValue() == null &&
                        newValue == null);
            });

            searchDialog.setResultConverter(dialogButton -> {
                if (dialogButton == searchButtonType) {
                    // Perform search and show results
                    performSearch(txtSearch.getText(), cmbMood.getValue(), datePicker.getValue());
                }
                return null;
            });

            searchDialog.showAndWait();

        } catch (Exception e) {
            showError("Search Error", e.getMessage());
        }
    }

    private void performSearch(String query, String mood, LocalDate date) {
        try {
            java.util.Date utilDate = null;
            if (date != null) {
                utilDate = java.sql.Date.valueOf(date);
            }

            var results = diaryManager.searchEntries(query, mood, utilDate);

            // Create results window
            Stage resultsStage = new Stage();
            resultsStage.setTitle("Search Results");
            resultsStage.initModality(Modality.WINDOW_MODAL);
            resultsStage.initOwner(mainContainer.getScene().getWindow());

            ListView<DiaryEntry> resultsList = new ListView<>();
            ObservableList<DiaryEntry> items = FXCollections.observableArrayList(results);
            resultsList.setItems(items);
            resultsList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(DiaryEntry entry, boolean empty) {
                    super.updateItem(entry, empty);
                    if (empty || entry == null) {
                        setText(null);
                    } else {
                        setText(entry.getTitle() + " - " + entry.getFormattedDate());
                        setTooltip(new Tooltip(entry.getPreview(100)));
                    }
                }
            });

            resultsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    DiaryEntry selected = resultsList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        openEditor(selected);
                        resultsStage.close();
                    }
                }
            });

            VBox root = new VBox(10);
            root.setPadding(new javafx.geometry.Insets(10));
            root.getChildren().addAll(
                    new Label("Found " + results.size() + " entries:"),
                    resultsList
            );

            Scene scene = new Scene(root, 400, 300);
            resultsStage.setScene(scene);
            resultsStage.show();

            updateStatus("Search completed: " + results.size() + " results");

        } catch (Exception e) {
            showError("Search Failed", e.getMessage());
        }
    }

    private void openSettings() {
        Alert settingsAlert = new Alert(Alert.AlertType.INFORMATION);
        settingsAlert.setTitle("Settings");
        settingsAlert.setHeaderText("Application Settings");
        settingsAlert.setContentText("Settings feature coming soon!\n\nCurrent features:\n• Light/Dark theme\n• Auto-save\n• Rich text editor\n• File management");
        settingsAlert.showAndWait();
    }

    private void toggleTheme() {
        darkMode = !darkMode;

        Scene scene = mainContainer.getScene();
        scene.getStylesheets().clear();

        if (darkMode) {
            scene.getStylesheets().add(getClass().getResource("/com/diarymanager/css/styles-dark.css").toExternalForm());
            btnThemeToggle.setText("☀️ Light Mode");
            updateStatus("Dark mode enabled");
        } else {
            scene.getStylesheets().add(getClass().getResource("/com/diarymanager/css/styles-light.css").toExternalForm());
            btnThemeToggle.setText("🌙 Dark Mode");
            updateStatus("Light mode enabled");
        }
    }

    private void updateStatus(String message) {
        lblStatus.setText(message);

        // Clear status after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    if (lblStatus.getText().equals(message)) {
                        lblStatus.setText("Ready");
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}