package com.neurabot.util;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Displays elegant toast-style notifications in the bottom-right corner.
 */
public class NotificationManager {

    public enum NotificationType { SUCCESS, INFO, WARNING, ERROR }

    private final Stage ownerStage;

    public NotificationManager(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void show(String message, NotificationType type) {
        Popup popup = new Popup();

        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(340);
        container.setMinHeight(56);
        container.setPadding(new javafx.geometry.Insets(14, 20, 14, 16));

        String icon = getIcon(type);
        String bgColor = getBgColor(type);
        String borderColor = getBorderColor(type);

        container.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 16, 0, 0, 4);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: #E6EDF3; -fx-font-size: 13px; -fx-font-weight: 500; -fx-wrap-text: true;");
        msgLabel.setMaxWidth(270);

        container.getChildren().addAll(iconLabel, msgLabel);
        popup.getContent().add(container);
        popup.setAutoHide(true);

        // Position: bottom-right of stage
        popup.setOnShown(e -> {
            double x = ownerStage.getX() + ownerStage.getWidth() - 360;
            double y = ownerStage.getY() + ownerStage.getHeight() - 100;
            popup.setX(x);
            popup.setY(y);
        });

        popup.show(ownerStage);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Slide in from right
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), container);
        slideIn.setFromX(60);
        slideIn.setToX(0);
        slideIn.play();

        // Auto dismiss after 3.5s
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(3.5));
        pause.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), container);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> popup.hide());
            fadeOut.play();
        });
        pause.play();
    }

    public void success(String message) { show(message, NotificationType.SUCCESS); }
    public void info(String message)    { show(message, NotificationType.INFO); }
    public void warning(String message) { show(message, NotificationType.WARNING); }
    public void error(String message)   { show(message, NotificationType.ERROR); }

    private String getIcon(NotificationType type) {
        return switch (type) {
            case SUCCESS -> "✅";
            case INFO    -> "ℹ️";
            case WARNING -> "⚠️";
            case ERROR   -> "❌";
        };
    }

    private String getBgColor(NotificationType type) {
        return switch (type) {
            case SUCCESS -> "rgba(34, 197, 94, 0.15)";
            case INFO    -> "rgba(59, 130, 246, 0.15)";
            case WARNING -> "rgba(245, 158, 11, 0.15)";
            case ERROR   -> "rgba(239, 68, 68, 0.15)";
        };
    }

    private String getBorderColor(NotificationType type) {
        return switch (type) {
            case SUCCESS -> "rgba(34, 197, 94, 0.4)";
            case INFO    -> "rgba(59, 130, 246, 0.4)";
            case WARNING -> "rgba(245, 158, 11, 0.4)";
            case ERROR   -> "rgba(239, 68, 68, 0.4)";
        };
    }
}
