package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.database.DatabaseManager;
import com.neurabot.model.ActivityLog;
import com.neurabot.model.User;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Registration screen for creating a new NeuraBot AI account.
 */
public class RegisterScreen {

    private final Stage stage;
    private final DatabaseManager db;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
        this.db = DatabaseManager.getInstance();
    }

    public void show() {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: #0D1117;");

        // Left branding
        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Right form
        VBox rightPanel = buildRightPanel();
        rightPanel.setPrefWidth(480);
        rightPanel.setMinWidth(480);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 1100, 720);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(24);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #0D1117, #161B22);");

        Label icon = new Label("🚀");
        icon.setStyle("-fx-font-size: 64px;");

        Label brand = new Label("Join NeuraBot AI");
        brand.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:34px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");

        Label desc = new Label(
                "Create your free account and start\nexploring intelligent AI conversations.\n\nUnlock access to:\n• Smart AI chatbot\n• Knowledge Base\n• Analytics Dashboard\n• Admin Tools (for admins)"
        );
        desc.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-text-fill:#64748B;");

        panel.getChildren().addAll(icon, brand, desc);
        return panel;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-border-color: rgba(108,99,255,0.2); -fx-border-width: 0 0 0 1;"
        );

        VBox form = new VBox(16);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(40, 50, 40, 50));
        form.setMaxWidth(400);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label sub = new Label("Fill in the details below to get started");
        sub.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#64748B;");

        // Fields
        TextField fullNameField = createField("Full Name", "Mohammad Sakib Ahmad");
        TextField emailField = createField("Email Address", "you@example.com");
        TextField usernameField = createField("Username", "Choose a username");
        PasswordField passwordField = createPasswordField("Password", "Min 6 characters");
        PasswordField confirmField = createPasswordField("Confirm Password", "Re-enter password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:#EF4444; -fx-font-size:12px;");
        errorLabel.setWrapText(true);

        Button registerBtn = new Button("Create My Account  →");
        registerBtn.setPrefWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(44);
        applyPrimaryStyle(registerBtn);

        registerBtn.setOnAction(e -> handleRegister(
                fullNameField, emailField, usernameField,
                passwordField, confirmField, errorLabel
        ));

        // Back to login
        HBox loginRow = new HBox(5);
        loginRow.setAlignment(Pos.CENTER);
        Label hasAccount = new Label("Already have an account?");
        hasAccount.setStyle("-fx-text-fill:#64748B; -fx-font-size:13px;");
        Hyperlink loginLink = new Hyperlink("Sign In");
        loginLink.setStyle("-fx-text-fill:#6C63FF; -fx-font-size:13px; -fx-font-weight:600; -fx-border-color:transparent;");
        loginLink.setOnAction(e -> showLogin());
        loginRow.getChildren().addAll(hasAccount, loginLink);

        form.getChildren().addAll(title, sub, fullNameField, emailField,
                usernameField, passwordField, confirmField, errorLabel, registerBtn, loginRow);

        panel.getChildren().add(form);
        return panel;
    }

    private TextField createField(String label, String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(42);
        field.setStyle(
                "-fx-background-color: #0D1117; -fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #374151;" +
                        "-fx-background-radius: 10; -fx-border-color: rgba(108,99,255,0.2);" +
                        "-fx-border-radius: 10; -fx-border-width: 1;" +
                        "-fx-padding: 0 14 0 14; -fx-font-size: 13px; -fx-font-family:'Segoe UI';"
        );
        return field;
    }

    private PasswordField createPasswordField(String label, String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setPrefHeight(42);
        field.setStyle(
                "-fx-background-color: #0D1117; -fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #374151;" +
                        "-fx-background-radius: 10; -fx-border-color: rgba(108,99,255,0.2);" +
                        "-fx-border-radius: 10; -fx-border-width: 1;" +
                        "-fx-padding: 0 14 0 14; -fx-font-size: 13px; -fx-font-family:'Segoe UI';"
        );
        return field;
    }

    private void applyPrimaryStyle(Button btn) {
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family:'Segoe UI';"
        );
    }

    private void handleRegister(TextField fullName, TextField email, TextField username,
                                 PasswordField password, PasswordField confirm, Label error) {
        error.setText("");
        String fn = fullName.getText().trim();
        String em = email.getText().trim();
        String un = username.getText().trim();
        String pw = password.getText();
        String cp = confirm.getText();

        if (fn.isEmpty() || em.isEmpty() || un.isEmpty() || pw.isEmpty()) {
            error.setText("⚠  All fields are required."); return;
        }
        if (!em.contains("@") || !em.contains(".")) {
            error.setText("⚠  Please enter a valid email address."); return;
        }
        if (un.length() < 3) {
            error.setText("⚠  Username must be at least 3 characters."); return;
        }
        if (pw.length() < 6) {
            error.setText("⚠  Password must be at least 6 characters."); return;
        }
        if (!pw.equals(cp)) {
            error.setText("⚠  Passwords do not match."); return;
        }
        if (db.usernameExists(un)) {
            error.setText("⚠  This username is already taken. Try another."); return;
        }
        if (db.emailExists(em)) {
            error.setText("⚠  This email is already registered."); return;
        }

        String hash = DatabaseManager.hashPassword(pw);
        User newUser = new User(fn, em, un, hash);
        db.registerUser(newUser);

        // Show success and go to login
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Created!");
        alert.setHeaderText("✅ Welcome to NeuraBot AI, " + fn.split(" ")[0] + "!");
        alert.setContentText("Your account has been created successfully.\nYou can now sign in with your credentials.");
        alert.showAndWait();

        showLogin();
    }

    private void showLogin() {
        LoginScreen login = new LoginScreen(stage);
        login.show();
    }
}
