package com.neurabot;

import com.neurabot.database.DatabaseManager;
import com.neurabot.util.ThemeManager;
import com.neurabot.view.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * NeuraBot AI - Intelligent Conversational Assistant
 * Main Application Entry Point
 *
 * @author Mohammad Sakib Ahmad
 * @version 1.0
 */
public class App extends Application {

    public static Stage primaryStage;
    public static ThemeManager themeManager;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        themeManager = new ThemeManager();

        // Initialize database
        DatabaseManager.getInstance().initialize();

        // Configure stage
        stage.setTitle("NeuraBot AI – Intelligent Conversational Assistant");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.centerOnScreen();

        // Show splash screen first
        SplashScreen splash = new SplashScreen(stage);
        splash.show();
    }

    @Override
    public void stop() {
        // Save all data on exit
        DatabaseManager.getInstance().shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
