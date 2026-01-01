package com.diarymanager.view.components;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.HTMLEditor;
import java.util.Arrays;
import java.util.List;

public class RichTextToolbar extends ToolBar {
    private HTMLEditor editor;

    public RichTextToolbar(HTMLEditor editor) {
        this.editor = editor;
        initializeToolbar();
    }

    private void initializeToolbar() {
        // Font family dropdown
        ComboBox<String> fontFamilyCombo = new ComboBox<>();
        fontFamilyCombo.getItems().addAll(
                "Arial", "Times New Roman", "Courier New",
                "Georgia", "Verdana", "Comic Sans MS"
        );
        fontFamilyCombo.setPromptText("Font");
        fontFamilyCombo.setPrefWidth(120);

        // Font size dropdown
        ComboBox<String> fontSizeCombo = new ComboBox<>();
        fontSizeCombo.getItems().addAll("8", "10", "12", "14", "18", "24", "36", "48");
        fontSizeCombo.setPromptText("Size");
        fontSizeCombo.setPrefWidth(70);

        // Formatting buttons
        Button boldButton = createToolbarButton("B", "Bold", "bold-button");
        Button italicButton = createToolbarButton("I", "Italic", "italic-button");
        Button underlineButton = createToolbarButton("U", "Underline", "underline-button");

        // Alignment buttons
        Button alignLeftButton = createToolbarButton("⬅", "Align Left", "align-button");
        Button alignCenterButton = createToolbarButton("⬌", "Align Center", "align-button");
        Button alignRightButton = createToolbarButton("➡", "Align Right", "align-button");

        // List buttons
        Button bulletListButton = createToolbarButton("•", "Bullet List", "list-button");
        Button numberedListButton = createToolbarButton("1.", "Numbered List", "list-button");

        // Color buttons
        Button textColorButton = createToolbarButton("A", "Text Color", "color-button");
        Button highlightButton = createToolbarButton("🖍", "Highlight", "highlight-button");

        // Link button
        Button linkButton = createToolbarButton("🔗", "Insert Link", "link-button");

        // Clear formatting
        Button clearFormatButton = createToolbarButton("✕", "Clear Formatting", "clear-button");

        // Add all components to toolbar
        this.getItems().addAll(
                fontFamilyCombo,
                fontSizeCombo,
                new Separator(),
                boldButton, italicButton, underlineButton,
                new Separator(),
                alignLeftButton, alignCenterButton, alignRightButton,
                new Separator(),
                bulletListButton, numberedListButton,
                new Separator(),
                textColorButton, highlightButton,
                new Separator(),
                linkButton,
                new Separator(),
                clearFormatButton
        );

        // Setup button actions
        setupButtonActions(boldButton, italicButton, underlineButton,
                alignLeftButton, alignCenterButton, alignRightButton,
                bulletListButton, numberedListButton,
                textColorButton, highlightButton, linkButton,
                clearFormatButton, fontFamilyCombo, fontSizeCombo);
    }

    private Button createToolbarButton(String text, String tooltip, String styleClass) {
        Button button = new Button(text);
        button.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        button.getStyleClass().add(styleClass);
        button.setPrefSize(32, 32);
        return button;
    }

    private void setupButtonActions(Button bold, Button italic, Button underline,
                                    Button alignLeft, Button alignCenter, Button alignRight,
                                    Button bulletList, Button numberedList,
                                    Button textColor, Button highlight, Button link,
                                    Button clearFormat, ComboBox<String> fontFamily,
                                    ComboBox<String> fontSize) {

        // Basic formatting
        bold.setOnAction(e -> executeCommand("bold"));
        italic.setOnAction(e -> executeCommand("italic"));
        underline.setOnAction(e -> executeCommand("underline"));

        // Alignment
        alignLeft.setOnAction(e -> executeCommand("justifyleft"));
        alignCenter.setOnAction(e -> executeCommand("justifycenter"));
        alignRight.setOnAction(e -> executeCommand("justifyright"));

        // Lists
        bulletList.setOnAction(e -> executeCommand("insertUnorderedList"));
        numberedList.setOnAction(e -> executeCommand("insertOrderedList"));

        // Colors (simplified - would need color picker dialogs in real implementation)
        textColor.setOnAction(e -> {
            javafx.scene.control.ColorPicker colorPicker = new javafx.scene.control.ColorPicker();
            colorPicker.setOnAction(colorEvent -> {
                String color = String.format("#%02X%02X%02X",
                        (int)(colorPicker.getValue().getRed() * 255),
                        (int)(colorPicker.getValue().getGreen() * 255),
                        (int)(colorPicker.getValue().getBlue() * 255));
                executeCommand("forecolor", color);
            });
            colorPicker.show();
        });

        highlight.setOnAction(e -> {
            javafx.scene.control.ColorPicker colorPicker = new javafx.scene.control.ColorPicker();
            colorPicker.setOnAction(colorEvent -> {
                String color = String.format("#%02X%02X%02X",
                        (int)(colorPicker.getValue().getRed() * 255),
                        (int)(colorPicker.getValue().getGreen() * 255),
                        (int)(colorPicker.getValue().getBlue() * 255));
                executeCommand("backcolor", color);
            });
            colorPicker.show();
        });

        // Link
        link.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("https://");
            dialog.setTitle("Insert Link");
            dialog.setHeaderText("Enter URL:");
            dialog.setContentText("URL:");

            dialog.showAndWait().ifPresent(url -> {
                if (!url.isEmpty()) {
                    executeCommand("createlink", url);
                }
            });
        });

        // Clear formatting
        clearFormat.setOnAction(e -> executeCommand("removeformat"));

        // Font family
        fontFamily.setOnAction(e -> {
            if (fontFamily.getValue() != null) {
                executeCommand("fontname", fontFamily.getValue());
            }
        });

        // Font size
        fontSize.setOnAction(e -> {
            if (fontSize.getValue() != null) {
                executeCommand("fontsize", fontSize.getValue());
            }
        });
    }

    private void executeCommand(String command) {
        executeCommand(command, null);
    }

    private void executeCommand(String command, String value) {
        // Note: HTMLEditor doesn't expose its WebView directly
        // In a production app, you'd need to access the WebEngine
        // This is a simplified version

        System.out.println("Executing command: " + command + (value != null ? " = " + value : ""));

        // For demonstration, we'll just show what would happen
        // In reality, you'd need to use JavaScript to manipulate the editor
        if (editor != null) {
            // This is where you'd execute JavaScript on the editor's WebEngine
            // Example: webEngine.executeScript("document.execCommand('" + command + "', false, '" + value + "')");
        }
    }
}