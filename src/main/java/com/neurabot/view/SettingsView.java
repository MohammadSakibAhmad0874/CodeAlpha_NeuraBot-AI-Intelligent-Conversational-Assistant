package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.database.DatabaseManager;
import com.neurabot.model.ActivityLog;
import com.neurabot.model.User;
import com.neurabot.model.UserSettings;
import com.neurabot.util.NotificationManager;
import javafx.animation.FadeTransition;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * User settings & customization panel.
 */
public class SettingsView {

    private final Stage stage;
    private final User user;
    private final DatabaseManager db;
    private final NotificationManager notif;
    private UserSettings settings;

    public SettingsView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.db = DatabaseManager.getInstance();
        this.notif = new NotificationManager(stage);
        this.settings = db.getUserSettings(user.getId());
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0D1117;");
        root.setLeft(buildSidebar());
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1280, 800);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(0); fade.setToValue(1); fade.play();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:#161B22; -fx-border-color:rgba(108,99,255,0.15); -fx-border-width:0 1 0 0;");

        Button backBtn = new Button("← Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-background-color:rgba(108,99,255,0.1); -fx-text-fill:#A78BFA; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
        backBtn.setOnAction(e -> goBack());

        sidebar.getChildren().addAll(backBtn);
        return sidebar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(28, 40, 32, 40));

        // Header
        VBox header = new VBox(4);
        Label title = new Label("⚙️ Settings");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Customize your NeuraBot AI experience");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");
        header.getChildren().addAll(title, sub);

        content.getChildren().addAll(header,
                buildThemeSection(),
                buildPersonalitySection(),
                buildDisplaySection(),
                buildNotificationSection(),
                buildSaveButton());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    // ─── THEME SECTION ───────────────────────────────────────────────────────

    private VBox buildThemeSection() {
        VBox section = buildSection("🎨 Appearance", "Choose how NeuraBot AI looks");

        // Theme toggle
        HBox themeRow = buildSettingRow("Theme", "");
        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton darkBtn = new RadioButton("🌙 Dark Mode");
        darkBtn.setToggleGroup(themeGroup);
        darkBtn.setStyle("-fx-text-fill:#94A3B8; -fx-font-size:13px;");
        RadioButton lightBtn = new RadioButton("☀ Light Mode");
        lightBtn.setToggleGroup(themeGroup);
        lightBtn.setStyle("-fx-text-fill:#94A3B8; -fx-font-size:13px;");

        if ("dark".equals(settings.getTheme())) darkBtn.setSelected(true);
        else lightBtn.setSelected(true);

        themeGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == darkBtn) {
                settings.setTheme("dark");
                App.themeManager.setTheme(com.neurabot.util.ThemeManager.Theme.DARK);
            } else {
                settings.setTheme("light");
                App.themeManager.setTheme(com.neurabot.util.ThemeManager.Theme.LIGHT);
            }
        });

        HBox themeButtons = new HBox(20, darkBtn, lightBtn);
        themeButtons.setAlignment(Pos.CENTER_LEFT);
        ((HBox) themeRow.getChildren().get(0)).getChildren().addAll(themeButtons);
        section.getChildren().add(themeRow);

        // Chat bubble style
        HBox bubbleRow = buildSettingRow("Chat Bubble Style", "How message bubbles appear");
        ComboBox<String> bubbleCombo = new ComboBox<>();
        bubbleCombo.getItems().addAll("Rounded", "Flat", "Minimal");
        bubbleCombo.setValue(capitalize(settings.getChatBubbleStyle()));
        bubbleCombo.setStyle("-fx-background-color:#161B22; -fx-text-fill:white; -fx-font-size:13px;");
        bubbleCombo.valueProperty().addListener((obs, old, val) -> settings.setChatBubbleStyle(val.toLowerCase()));
        ((HBox) bubbleRow.getChildren().get(0)).getChildren().add(bubbleCombo);
        section.getChildren().add(bubbleRow);

        return section;
    }

    // ─── AI PERSONALITY ──────────────────────────────────────────────────────

    private VBox buildPersonalitySection() {
        VBox section = buildSection("🤖 AI Personality", "Choose how NeuraBot communicates with you");

        HBox row = buildSettingRow("AI Mode", "Affects tone and style of responses");
        ToggleGroup group = new ToggleGroup();

        String[][] personalities = {
                {"friendly", "😊 Friendly"},
                {"professional", "💼 Professional"},
                {"teacher", "📖 Teacher"},
                {"coder", "💻 Coding Mentor"},
                {"expert", "🧠 Expert"}
        };

        VBox radioGroup = new VBox(8);
        for (String[] p : personalities) {
            RadioButton rb = new RadioButton(p[1]);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill:#94A3B8; -fx-font-size:13px;");
            if (p[0].equals(settings.getAiPersonality())) rb.setSelected(true);
            final String key = p[0];
            rb.setOnAction(e -> {
                settings.setAiPersonality(key);
                user.setAiPersonality(key);
            });
            radioGroup.getChildren().add(rb);
        }

        ((HBox) row.getChildren().get(0)).getChildren().add(radioGroup);
        section.getChildren().add(row);
        return section;
    }

    // ─── DISPLAY SECTION ─────────────────────────────────────────────────────

    private VBox buildDisplaySection() {
        VBox section = buildSection("🖥 Display", "Adjust text and animation preferences");

        // Font size
        HBox fontRow = buildSettingRow("Font Size", "Text size in the chat interface");
        Slider fontSlider = new Slider(11, 20, settings.getFontSize());
        fontSlider.setShowTickLabels(true);
        fontSlider.setMajorTickUnit(3);
        fontSlider.setMinWidth(200);
        fontSlider.setStyle("-fx-accent:#6C63FF;");
        Label fontValue = new Label(String.format("%.0fpx", settings.getFontSize()));
        fontValue.setStyle("-fx-text-fill:#A78BFA; -fx-font-size:13px; -fx-min-width:36;");
        fontSlider.valueProperty().addListener((obs, old, val) -> {
            settings.setFontSize(val.doubleValue());
            fontValue.setText(String.format("%.0fpx", val.doubleValue()));
        });
        HBox fontControl = new HBox(12, fontSlider, fontValue);
        fontControl.setAlignment(Pos.CENTER_LEFT);
        ((HBox) fontRow.getChildren().get(0)).getChildren().add(fontControl);
        section.getChildren().add(fontRow);

        // Animation speed
        HBox animRow = buildSettingRow("Animation Speed", "Speed of UI transitions and effects");
        Slider animSlider = new Slider(0.5, 2.0, settings.getAnimationSpeed());
        animSlider.setShowTickLabels(true);
        animSlider.setMajorTickUnit(0.5);
        animSlider.setMinWidth(200);
        animSlider.setStyle("-fx-accent:#6C63FF;");
        Label animValue = new Label(String.format("%.1fx", settings.getAnimationSpeed()));
        animValue.setStyle("-fx-text-fill:#A78BFA; -fx-font-size:13px; -fx-min-width:36;");
        animSlider.valueProperty().addListener((obs, old, val) -> {
            settings.setAnimationSpeed(val.doubleValue());
            animValue.setText(String.format("%.1fx", val.doubleValue()));
        });
        HBox animControl = new HBox(12, animSlider, animValue);
        animControl.setAlignment(Pos.CENTER_LEFT);
        ((HBox) animRow.getChildren().get(0)).getChildren().add(animControl);
        section.getChildren().add(animRow);

        // Show timestamps
        HBox tsRow = buildSettingRow("Show Timestamps", "Display time on each message bubble");
        CheckBox tsCheck = new CheckBox();
        tsCheck.setSelected(settings.isShowTimestamps());
        tsCheck.setStyle("-fx-accent:#6C63FF;");
        tsCheck.setOnAction(e -> settings.setShowTimestamps(tsCheck.isSelected()));
        ((HBox) tsRow.getChildren().get(0)).getChildren().add(tsCheck);
        section.getChildren().add(tsRow);

        // Typing indicator
        HBox typingRow = buildSettingRow("Typing Indicator", "Show 'NeuraBot is thinking...' animation");
        CheckBox typingCheck = new CheckBox();
        typingCheck.setSelected(settings.isTypingIndicator());
        typingCheck.setStyle("-fx-accent:#6C63FF;");
        typingCheck.setOnAction(e -> settings.setTypingIndicator(typingCheck.isSelected()));
        ((HBox) typingRow.getChildren().get(0)).getChildren().add(typingCheck);
        section.getChildren().add(typingRow);

        return section;
    }

    // ─── NOTIFICATION SECTION ────────────────────────────────────────────────

    private VBox buildNotificationSection() {
        VBox section = buildSection("🔔 Notifications & Data", "Control alerts and data saving preferences");

        HBox notifRow = buildSettingRow("Notifications", "Show toast notifications for events");
        CheckBox notifCheck = new CheckBox();
        notifCheck.setSelected(settings.isNotificationsEnabled());
        notifCheck.setStyle("-fx-accent:#6C63FF;");
        notifCheck.setOnAction(e -> settings.setNotificationsEnabled(notifCheck.isSelected()));
        ((HBox) notifRow.getChildren().get(0)).getChildren().add(notifCheck);
        section.getChildren().add(notifRow);

        HBox autoSaveRow = buildSettingRow("Auto Save", "Automatically save conversations");
        CheckBox autoSaveCheck = new CheckBox();
        autoSaveCheck.setSelected(settings.isAutoSave());
        autoSaveCheck.setStyle("-fx-accent:#6C63FF;");
        autoSaveCheck.setOnAction(e -> settings.setAutoSave(autoSaveCheck.isSelected()));
        ((HBox) autoSaveRow.getChildren().get(0)).getChildren().add(autoSaveCheck);
        section.getChildren().add(autoSaveRow);

        // Language
        HBox langRow = buildSettingRow("Language", "Interface language");
        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Hindi");
        langCombo.setValue("en".equals(settings.getLanguage()) ? "English" : "Hindi");
        langCombo.setStyle("-fx-background-color:#161B22; -fx-text-fill:white;");
        langCombo.valueProperty().addListener((obs, old, val) -> settings.setLanguage("English".equals(val) ? "en" : "hi"));
        ((HBox) langRow.getChildren().get(0)).getChildren().add(langCombo);
        section.getChildren().add(langRow);

        return section;
    }

    // ─── SAVE BUTTON ─────────────────────────────────────────────────────────

    private HBox buildSaveButton() {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = new Button("💾  Save Settings");
        saveBtn.setStyle(
                "-fx-background-color:linear-gradient(to right,#6C63FF,#8B5CF6);" +
                "-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:700;" +
                "-fx-background-radius:10; -fx-padding:12 32; -fx-cursor:hand;"
        );
        saveBtn.setOnAction(e -> {
            db.saveUserSettings(settings);
            db.updateUser(user);
            db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.SETTINGS_CHANGED, "Settings saved");
            notif.success("✅ Settings saved successfully!");
        });

        Button resetBtn = new Button("Reset Defaults");
        resetBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:#64748B; -fx-font-size:13px; -fx-cursor:hand; -fx-border-color:rgba(100,116,139,0.3); -fx-border-radius:10; -fx-padding:12 20; -fx-background-radius:10;");
        resetBtn.setOnAction(e -> {
            settings = new UserSettings(user.getId());
            db.saveUserSettings(settings);
            notif.info("Settings reset to defaults. Reload to see changes.");
        });

        row.getChildren().addAll(saveBtn, resetBtn);
        return row;
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private VBox buildSection(String title, String description) {
        VBox section = new VBox(16);
        section.setPadding(new Insets(22));
        section.setStyle("-fx-background-color:#161B22; -fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:14; -fx-border-width:1;");

        VBox header = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label descLbl = new Label(description);
        descLbl.setStyle("-fx-font-size:12px; -fx-text-fill:#64748B;");
        header.getChildren().addAll(titleLbl, descLbl);

        section.getChildren().addAll(header, new Separator() {{
            setStyle("-fx-background-color:rgba(108,99,255,0.1);");
        }});
        return section;
    }

    private HBox buildSettingRow(String label, String hint) {
        HBox outerRow = new HBox();
        outerRow.setAlignment(Pos.CENTER_LEFT);

        VBox labelBox = new VBox(2);
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size:14px; -fx-font-weight:600; -fx-text-fill:white; -fx-min-width:200;");
        if (!hint.isEmpty()) {
            Label hintLbl = new Label(hint);
            hintLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#374151;");
            labelBox.getChildren().addAll(labelLbl, hintLbl);
        } else {
            labelBox.getChildren().add(labelLbl);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controlBox = new HBox();
        controlBox.setAlignment(Pos.CENTER_RIGHT);

        outerRow.getChildren().addAll(new HBox(labelBox, spacer, controlBox));
        return outerRow;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void goBack() {
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }
}
