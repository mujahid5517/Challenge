package com.chapter3_challenge_brandedweatherwidget;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        WeatherWidget weatherWidget = new WeatherWidget();
        
        Scene scene = new Scene(weatherWidget.getRoot(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/chapter3_challenge_brandedweatherwidget/style.css").toExternalForm());
        
        primaryStage.setTitle("Aero Dynamics - Flight Weather Widget");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}