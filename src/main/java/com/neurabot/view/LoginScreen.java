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
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Modern login screen with glassmorphism card design.
 */
public class LoginScreen {

    private final Stage stage;
    private final DatabaseManager db;
    public static User loggedInUser;

    public LoginScreen(Stage stage) {
        this.stage = stage;
        this.db = DatabaseManager.getInstance();
    }

    public void show() {
        stage.setWidth(1100);
        stage.setHeight(720);
        stage.centerOnScreen();

        // Split layout: left branding | right form
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: #0D1117;");

        // LEFT: Branding panel
        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // RIGHT: Login form
        VBox rightPanel = buildRightPanel();
        rightPanel.setPrefWidth(440);
        rightPanel.setMinWidth(440);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 1100, 720);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // ─── LEFT PANEL ──────────────────────────────────────────────────────────

    private VBox buildLeftPanel() {
        VBox panel = new VBox(30);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #0D1117, #161B22);");

        Label icon = new Label("🤖");
        icon.setStyle("-fx-font-size: 64px;");

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), icon);
        pulse.setFromX(0.9);
        pulse.setToX(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        Label brand = new Label("NeuraBot AI");
        brand.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:38px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");

        Label tagline = new Label("Think.  Learn.  Assist.");
        tagline.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-text-fill:#64748B; -fx-letter-spacing:2;");

        // Feature bullets
        VBox bullets = new VBox(14);
        bullets.setAlignment(Pos.CENTER_LEFT);
        bullets.setPadding(new Insets(20, 0, 0, 0));

        String[] features = {
                "🧠  Natural Language Processing",
                "💬  Smart Conversations",
                "📚  100+ Knowledge Articles",
                "📊  Real-time Analytics",
                "🎨  Dark & Light Themes"
        };
        for (String f : features) {
            Label lbl = new Label(f);
            lbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#94A3B8;");
            bullets.getChildren().add(lbl);
        }

        Label demoHint = new Label("Demo: admin / admin123  |  demo / demo123");
        demoHint.setStyle(
                "-fx-font-size:11px; -fx-text-fill:#374151;" +
                        "-fx-background-color: rgba(108,99,255,0.08);" +
                        "-fx-padding: 8 14 8 14; -fx-background-radius:8;"
        );

        panel.getChildren().addAll(icon, brand, tagline, bullets, demoHint);
        return panel;
    }

    // ─── RIGHT PANEL ─────────────────────────────────────────────────────────

    private VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-border-color: rgba(108,99,255,0.2);" +
                        "-fx-border-width: 0 0 0 1;"
        );

        VBox formCard = new VBox(22);
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setPadding(new Insets(50, 50, 50, 50));
        formCard.setMaxWidth(380);

        Label title = new Label("Welcome Back 👋");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label subtitle = new Label("Sign in to your NeuraBot account");
        subtitle.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-text-fill:#64748B;");

        // Username field
        VBox usernameBox = buildFieldGroup("Username", "Enter your username");
        TextField usernameField = (TextField) ((VBox) usernameBox).getChildren().get(1);

        // Password field
        VBox passwordBox = buildPasswordGroup();
        PasswordField passwordField = (PasswordField) ((HBox) ((VBox) passwordBox).getChildren().get(1)).getChildren().get(0);

        // Options row
        HBox optionsRow = new HBox();
        optionsRow.setAlignment(Pos.CENTER_LEFT);
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill:#94A3B8; -fx-font-size:12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setStyle("-fx-text-fill:#6C63FF; -fx-font-size:12px; -fx-border-color:transparent;");
        forgotLink.setOnAction(e -> showForgotPassword());

        optionsRow.getChildren().addAll(rememberMe, spacer, forgotLink);

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:#EF4444; -fx-font-size:12px;");
        errorLabel.setWrapText(true);

        // Login button
        Button loginBtn = new Button("Sign In  →");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(46);
        loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family:'Segoe UI';"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #7C73FF, #9B6CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                        "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.5), 12, 0, 0, 3);"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family:'Segoe UI';"
        ));

        loginBtn.setOnAction(e -> handleLogin(usernameField, passwordField, errorLabel));

        // Enter key support
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin(usernameField, passwordField, errorLabel));

        // Divider
        HBox divider = new HBox(10);
        divider.setAlignment(Pos.CENTER);
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        HBox.setHgrow(sep1, Priority.ALWAYS);
        HBox.setHgrow(sep2, Priority.ALWAYS);
        Label or = new Label("OR");
        or.setStyle("-fx-text-fill:#374151; -fx-font-size:11px;");
        divider.getChildren().addAll(sep1, or, sep2);

        // Register link
        HBox registerRow = new HBox(5);
        registerRow.setAlignment(Pos.CENTER);
        Label noAccount = new Label("Don't have an account?");
        noAccount.setStyle("-fx-text-fill:#64748B; -fx-font-size:13px;");
        Hyperlink registerLink = new Hyperlink("Create Account");
        registerLink.setStyle("-fx-text-fill:#6C63FF; -fx-font-size:13px; -fx-font-weight:600; -fx-border-color:transparent;");
        registerLink.setOnAction(e -> showRegister());
        registerRow.getChildren().addAll(noAccount, registerLink);

        formCard.getChildren().addAll(
                title, subtitle, usernameBox, passwordBox,
                optionsRow, errorLabel, loginBtn, divider, registerRow
        );

        panel.getChildren().add(formCard);

        // Slide in
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), panel);
        slide.setFromX(40);
        slide.setToX(0);
        slide.play();

        return panel;
    }

    private VBox buildFieldGroup(String label, String placeholder) {
        VBox group = new VBox(8);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-font-weight:600; -fx-text-fill:#94A3B8;");

        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(44);
        field.setStyle(
                "-fx-background-color: #0D1117; -fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #374151;" +
                        "-fx-background-radius: 10; -fx-border-color: rgba(108,99,255,0.2);" +
                        "-fx-border-radius: 10; -fx-border-width: 1;" +
                        "-fx-padding: 0 14 0 14; -fx-font-size: 14px; -fx-font-family:'Segoe UI';"
        );
        field.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                field.setStyle(field.getStyle().replace("rgba(108,99,255,0.2)", "#6C63FF"));
            } else {
                field.setStyle(field.getStyle().replace("#6C63FF", "rgba(108,99,255,0.2)"));
            }
        });

        group.getChildren().addAll(lbl, field);
        return group;
    }

    private VBox buildPasswordGroup() {
        VBox group = new VBox(8);
        Label lbl = new Label("Password");
        lbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-font-weight:600; -fx-text-fill:#94A3B8;");

        PasswordField pf = new PasswordField();
        pf.setPromptText("Enter your password");
        pf.setPrefHeight(44);
        pf.setStyle(
                "-fx-background-color: #0D1117; -fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #374151;" +
                        "-fx-background-radius: 10 0 0 10; -fx-border-color: rgba(108,99,255,0.2);" +
                        "-fx-border-radius: 10 0 0 10; -fx-border-width: 1 0 1 1;" +
                        "-fx-padding: 0 14 0 14; -fx-font-size: 14px; -fx-font-family:'Segoe UI';"
        );

        HBox.setHgrow(pf, Priority.ALWAYS);
        HBox fieldRow = new HBox(0, pf);
        group.getChildren().addAll(lbl, fieldRow);
        return group;
    }

    // ─── ACTIONS ─────────────────────────────────────────────────────────────

    private void handleLogin(TextField usernameField, PasswordField passwordField, Label errorLabel) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        errorLabel.setText("");

        if (username.isEmpty()) { errorLabel.setText("⚠  Please enter your username."); return; }
        if (password.isEmpty()) { errorLabel.setText("⚠  Please enter your password."); return; }

        User user = db.authenticate(username, password);
        if (user == null) {
            errorLabel.setText("❌  Invalid username or password. Please try again.");
            // Shake animation
            TranslateTransition shake = new TranslateTransition(Duration.millis(60), usernameField);
            shake.setByX(8);
            shake.setAutoReverse(true);
            shake.setCycleCount(6);
            shake.play();
            return;
        }

        db.updateUserLastLogin(user.getId());
        db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.LOGIN,
                "User logged in successfully");

        loggedInUser = user;

        // Transition to dashboard
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }

    private void showRegister() {
        RegisterScreen register = new RegisterScreen(stage);
        register.show();
    }

    private void showForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Recovery");
        alert.setHeaderText("Reset Your Password");
        alert.setContentText(
                "Password recovery simulation:\n\n" +
                        "In a production system, a reset link would be\n" +
                        "sent to your registered email address.\n\n" +
                        "For now, use the demo credentials:\n" +
                        "  Admin: admin / admin123\n" +
                        "  Demo:  demo / demo123"
        );
        alert.showAndWait();
    }
}
