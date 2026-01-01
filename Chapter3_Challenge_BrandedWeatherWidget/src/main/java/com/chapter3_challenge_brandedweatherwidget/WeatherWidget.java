package com.chapter3_challenge_brandedweatherwidget;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;

public class WeatherWidget {
    private BorderPane root;
    private TextField cityField;
    private Button refreshButton;
    private Shape aircraftShape;
    
    public WeatherWidget() {
        initializeUI();
        setupBindings();
        setupAnimation();
    }
    
    private void initializeUI() {
        root = new BorderPane();
        root.getStyleClass().add("root");
        
        // TOP: City name and input
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // CENTER: Main weather display
        VBox centerSection = createCenterSection();
        root.setCenter(centerSection);
        
        // RIGHT: Aviation metrics
        VBox rightSection = createRightSection();
        root.setRight(rightSection);
        
        // BOTTOM: 3-day forecast
        HBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
    }
    
    private VBox createTopSection() {
        Label title = new Label("AERO DYNAMICS");
        title.getStyleClass().add("company-title");
        
        Label widgetTitle = new Label("FLIGHT WEATHER MONITOR");
        widgetTitle.getStyleClass().add("widget-title");
        
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        
        Label cityLabel = new Label("Airport/City:");
        cityLabel.getStyleClass().add("input-label");
        
        cityField = new TextField();
        cityField.setPromptText("Enter ICAO code or city");
        cityField.getStyleClass().add("city-input");
        cityField.setPrefWidth(200);
        
        refreshButton = new Button("GET WEATHER");
        refreshButton.getStyleClass().add("refresh-button");
        
        inputBox.getChildren().addAll(cityLabel, cityField, refreshButton);
        
        VBox topSection = new VBox(15);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20, 0, 20, 0));
        topSection.getChildren().addAll(title, widgetTitle, inputBox);
        
        return topSection;
    }
    
    private VBox createCenterSection() {
        VBox centerSection = new VBox(20);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(20));
        
        // City name display
        Label cityDisplay = new Label("KJFK - John F. Kennedy Intl");
        cityDisplay.getStyleClass().add("city-display");
        
        // Main temperature
        Label temperature = new Label("68°F");
        temperature.getStyleClass().add("temperature");
        
        // Weather condition
        Label condition = new Label("CLEAR SKIES");
        condition.getStyleClass().add("condition");
        
        // Aircraft shape
        aircraftShape = createAircraftShape();
        aircraftShape.getStyleClass().add("aircraft-shape");
        
        centerSection.getChildren().addAll(cityDisplay, temperature, condition, aircraftShape);
        
        return centerSection;
    }
    
    private Shape createAircraftShape() {
        // Create aircraft fuselage
        Polygon fuselage = new Polygon();
        fuselage.getPoints().addAll(
            0.0, -10.0,
            40.0, 0.0,
            0.0, 10.0,
            -10.0, 0.0
        );
        
        // Create wings
        Polygon wings = new Polygon();
        wings.getPoints().addAll(
            -30.0, 0.0,
            30.0, 0.0,
            20.0, -2.0,
            -20.0, -2.0
        );
        
        // Create tail
        Polygon tail = new Polygon();
        tail.getPoints().addAll(
            -35.0, 0.0,
            -25.0, 0.0,
            -25.0, -15.0,
            -35.0, -5.0
        );
        
        // Combine shapes
        Shape aircraft = Shape.union(fuselage, wings);
        aircraft = Shape.union(aircraft, tail);
        
        aircraft.setScaleX(1.5);
        aircraft.setScaleY(1.5);
        
        return aircraft;
    }
    
    private VBox createRightSection() {
        VBox rightSection = new VBox(15);
        rightSection.setAlignment(Pos.CENTER_LEFT);
        rightSection.setPadding(new Insets(20, 40, 20, 20));
        rightSection.getStyleClass().add("metrics-panel");
        
        // Flight Conditions
        Label conditionsTitle = new Label("FLIGHT CONDITIONS");
        conditionsTitle.getStyleClass().add("metric-title");
        
        Label conditionsValue = new Label("GOOD");
        conditionsValue.getStyleClass().addAll("metric-value", "condition-good");
        
        // Wind Speed & Direction
        Label windTitle = new Label("WIND");
        windTitle.getStyleClass().add("metric-title");
        
        HBox windBox = new HBox(10);
        windBox.setAlignment(Pos.CENTER_LEFT);
        
        // Wind direction arrow
        Polygon windArrow = new Polygon();
        windArrow.getPoints().addAll(
            0.0, -15.0,
            -10.0, 0.0,
            10.0, 0.0
        );
        windArrow.setRotate(45); // Northeast wind
        windArrow.getStyleClass().add("wind-arrow");
        
        Label windValue = new Label("12 kts | 045°");
        windValue.getStyleClass().add("metric-value");
        
        windBox.getChildren().addAll(windArrow, windValue);
        
        // Visibility
        Label visibilityTitle = new Label("VISIBILITY");
        visibilityTitle.getStyleClass().add("metric-title");
        
        Label visibilityValue = new Label("10+ SM");
        visibilityValue.getStyleClass().add("metric-value");
        
        rightSection.getChildren().addAll(
            conditionsTitle, conditionsValue,
            new Region() {{ setPrefHeight(15); }},
            windTitle, windBox,
            new Region() {{ setPrefHeight(15); }},
            visibilityTitle, visibilityValue
        );
        
        return rightSection;
    }
    
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));
        bottomSection.getStyleClass().add("forecast-panel");
        
        // Day 1 Forecast
        VBox day1 = createForecastDay("TODAY", "72°F", "Sunny", "GOOD");
        
        // Day 2 Forecast
        VBox day2 = createForecastDay("TOMORROW", "68°F", "Cloudy", "MARGINAL");
        
        // Day 3 Forecast
        VBox day3 = createForecastDay("DAY 3", "65°F", "Rain", "POOR");
        
        bottomSection.getChildren().addAll(day1, day2, day3);
        
        return bottomSection;
    }
    
    private VBox createForecastDay(String day, String temp, String condition, String flightCond) {
        VBox dayBox = new VBox(8);
        dayBox.setAlignment(Pos.CENTER);
        dayBox.getStyleClass().add("forecast-day");
        
        Label dayLabel = new Label(day);
        dayLabel.getStyleClass().add("forecast-day-label");
        
        Label tempLabel = new Label(temp);
        tempLabel.getStyleClass().add("forecast-temp");
        
        Label condLabel = new Label(condition);
        condLabel.getStyleClass().add("forecast-cond");
        
        Label flightLabel = new Label(flightCond);
        flightLabel.getStyleClass().add("forecast-flight");
        
        // Color code flight conditions
        if ("GOOD".equals(flightCond)) {
            flightLabel.getStyleClass().add("condition-good");
        } else if ("MARGINAL".equals(flightCond)) {
            flightLabel.getStyleClass().add("condition-marginal");
        } else {
            flightLabel.getStyleClass().add("condition-poor");
        }
        
        dayBox.getChildren().addAll(dayLabel, tempLabel, condLabel, flightLabel);
        
        return dayBox;
    }
    
    private void setupBindings() {
        // Disable refresh button if city field is empty
        refreshButton.disableProperty().bind(
            Bindings.isEmpty(cityField.textProperty())
        );
        
        // Add refresh action
        refreshButton.setOnAction(e -> {
            // In a real app, this would fetch new weather data
            System.out.println("Refreshing weather for: " + cityField.getText());
        });
    }
    
    private void setupAnimation() {
        // Subtle pulsating animation for aircraft
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(aircraftShape.scaleXProperty(), 1.5),
                new KeyValue(aircraftShape.scaleYProperty(), 1.5)
            ),
            new KeyFrame(Duration.seconds(2),
                new KeyValue(aircraftShape.scaleXProperty(), 1.6),
                new KeyValue(aircraftShape.scaleYProperty(), 1.6)
            )
        );
        
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }
    
    public BorderPane getRoot() {
        return root;
    }
}