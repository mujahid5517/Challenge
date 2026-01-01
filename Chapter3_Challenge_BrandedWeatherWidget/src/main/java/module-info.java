module com.chapter3_challenge_brandedweatherwidget {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.chapter3_challenge_brandedweatherwidget to javafx.fxml;
    exports com.chapter3_challenge_brandedweatherwidget;
}