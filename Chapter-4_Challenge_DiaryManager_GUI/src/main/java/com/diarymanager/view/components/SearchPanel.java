package com.diarymanager.view.components;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SearchPanel extends VBox {
    private TextField searchField;
    private ComboBox<String> moodCombo;
    private DatePicker datePicker;
    private Button searchButton;
    private Button clearButton;
    private Label resultsLabel;
    private ListView<String> resultsList;

    private Runnable onSearchCallback;
    private Runnable onClearCallback;

    public SearchPanel() {
        initializeUI();
        setupEvents();
    }

    private void initializeUI() {
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5;");

        // Title
        Label titleLabel = new Label("🔍 Advanced Search");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search in titles and content...");
        searchField.setPrefHeight(35);

        // Mood filter
        HBox moodBox = new HBox(10);
        moodBox.setAlignment(Pos.CENTER_LEFT);
        Label moodLabel = new Label("Mood:");
        moodLabel.setPrefWidth(80);

        moodCombo = new ComboBox<>();
        moodCombo.setPromptText("Any mood");
        moodCombo.setPrefWidth(200);
        moodCombo.getItems().addAll(
                "Any mood",
                "😊 Happy",
                "😢 Sad",
                "🤩 Excited",
                "😌 Calm",
                "😠 Angry",
                "🤔 Thoughtful",
                "😴 Tired",
                "🤗 Grateful"
        );
        moodCombo.getSelectionModel().selectFirst();

        moodBox.getChildren().addAll(moodLabel, moodCombo);

        // Date filter
        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("Date:");
        dateLabel.setPrefWidth(80);

        datePicker = new DatePicker();
        datePicker.setPromptText("Any date");
        datePicker.setPrefWidth(200);

        dateBox.getChildren().addAll(dateLabel, datePicker);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setPrefWidth(100);

        searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold;");
        searchButton.setPrefWidth(100);

        buttonBox.getChildren().addAll(clearButton, searchButton);

        // Results section
        resultsLabel = new Label("Results will appear here");
        resultsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6c757d;");

        resultsList = new ListView<>();
        resultsList.setPrefHeight(200);
        resultsList.setStyle("-fx-border-color: #ced4da; -fx-border-radius: 3;");

        // Add all components
        this.getChildren().addAll(
                titleLabel,
                searchField,
                moodBox,
                dateBox,
                buttonBox,
                new Separator(),
                resultsLabel,
                resultsList
        );
    }

    private void setupEvents() {
        searchButton.setOnAction(e -> performSearch());
        clearButton.setOnAction(e -> clearSearch());

        // Enable search button only when there's some input
        searchButton.disableProperty().bind(
                searchField.textProperty().isEmpty()
                        .and(moodCombo.valueProperty().isEqualTo("Any mood"))
                        .and(datePicker.valueProperty().isNull())
        );
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String mood = moodCombo.getValue();
        LocalDate date = datePicker.getValue();

        // Convert "Any mood" to null
        if ("Any mood".equals(mood)) {
            mood = null;
        }

        // Update results label
        StringBuilder searchInfo = new StringBuilder("Searching for: ");
        if (!query.isEmpty()) {
            searchInfo.append("'").append(query).append("' ");
        }
        if (mood != null) {
            searchInfo.append(" with mood ").append(mood).append(" ");
        }
        if (date != null) {
            searchInfo.append(" on ").append(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }

        resultsLabel.setText(searchInfo.toString());

        // Clear previous results
        resultsList.getItems().clear();

        // Add placeholder results (in real app, these would come from DiaryManager)
        ObservableList<String> placeholderResults = FXCollections.observableArrayList(
                "Sample result 1 - " + (query.isEmpty() ? "Diary entry" : query),
                "Sample result 2 - Another entry",
                "Sample result 3 - Test entry"
        );

        resultsList.setItems(placeholderResults);

        // Call callback if set
        if (onSearchCallback != null) {
            onSearchCallback.run();
        }
    }

    private void clearSearch() {
        searchField.clear();
        moodCombo.getSelectionModel().selectFirst();
        datePicker.setValue(null);
        resultsList.getItems().clear();
        resultsLabel.setText("Results will appear here");

        // Call callback if set
        if (onClearCallback != null) {
            onClearCallback.run();
        }
    }

    // Getters for search parameters
    public String getSearchQuery() {
        return searchField.getText().trim();
    }

    public String getSelectedMood() {
        String mood = moodCombo.getValue();
        return "Any mood".equals(mood) ? null : mood;
    }

    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    // Callback setters
    public void setOnSearch(Runnable callback) {
        this.onSearchCallback = callback;
    }

    public void setOnClear(Runnable callback) {
        this.onClearCallback = callback;
    }

    // Methods to update results
    public void setSearchResults(ObservableList<String> results) {
        resultsList.setItems(results);
        resultsLabel.setText("Found " + results.size() + " results");
    }

    public void clearResults() {
        resultsList.getItems().clear();
        resultsLabel.setText("No results to display");
    }

    public ListView<String> getResultsList() {
        return resultsList;
    }
}