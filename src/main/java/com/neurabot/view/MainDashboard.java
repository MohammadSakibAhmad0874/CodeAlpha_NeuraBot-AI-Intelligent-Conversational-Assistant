package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.analytics.AnalyticsManager;
import com.neurabot.database.DatabaseManager;
import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.ActivityLog;
import com.neurabot.model.ChatSession;
import com.neurabot.model.User;
import com.neurabot.util.NotificationManager;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

/**
 * Main dashboard — the hub after login.
 * Shows stats cards, recent conversations, and quick access to all features.
 */
public class MainDashboard {

    private final Stage stage;
    private final User user;
    private final DatabaseManager db;
    private final AnalyticsManager analytics;
    private final NotificationManager notif;

    public MainDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.db = DatabaseManager.getInstance();
        this.analytics = new AnalyticsManager();
        this.notif = new NotificationManager(stage);
    }

    public void show() {
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.centerOnScreen();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1117;");

        // Sidebar
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // Main content area
        ScrollPane scrollPane = new ScrollPane(buildMainContent());
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1280, 800);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        // Welcome notification
        javafx.application.Platform.runLater(() ->
                notif.success("Welcome back, " + user.getFullName().split(" ")[0] + "! 👋")
        );

        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-border-color: rgba(108,99,255,0.15); -fx-border-width: 0 1 0 0;"
        );

        // Logo
        HBox logo = new HBox(10);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.setPadding(new Insets(0, 0, 20, 8));
        Label logoIcon = new Label("🤖");
        logoIcon.setStyle("-fx-font-size: 22px;");
        Label logoText = new Label("NeuraBot AI");
        logoText.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#6C63FF;");
        logo.getChildren().addAll(logoIcon, logoText);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: rgba(108,99,255,0.15);");

        // Nav items
        VBox navItems = new VBox(2);
        navItems.setPadding(new Insets(10, 0, 0, 0));

        Button dashBtn    = createNavItem("🏠", "Dashboard",     true);
        Button chatBtn    = createNavItem("💬", "Chat",          false);
        Button kbBtn      = createNavItem("📚", "Knowledge Base",false);
        Button analyticsBtn = createNavItem("📊", "Analytics",   false);

        dashBtn.setOnAction(e -> {});
        chatBtn.setOnAction(e -> showChat());
        kbBtn.setOnAction(e -> showKnowledgeBase());
        analyticsBtn.setOnAction(e -> showAnalytics());

        navItems.getChildren().addAll(dashBtn, chatBtn, kbBtn, analyticsBtn);

        if (user.isAdmin()) {
            Button adminBtn = createNavItem("👑", "Admin Panel", false);
            adminBtn.setOnAction(e -> showAdminPanel());
            navItems.getChildren().add(adminBtn);
        }

        // Bottom section
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(108,99,255,0.15);");

        Button settingsBtn = createNavItem("⚙️", "Settings", false);
        Button aboutBtn    = createNavItem("ℹ️", "About",    false);
        settingsBtn.setOnAction(e -> showSettings());
        aboutBtn.setOnAction(e -> showAbout());

        // Theme toggle
        Button themeBtn = new Button(App.themeManager.getThemeIcon() + "  " + App.themeManager.getThemeName());
        themeBtn.setMaxWidth(Double.MAX_VALUE);
        themeBtn.setStyle(navStyle(false));
        themeBtn.setOnAction(e -> {
            App.themeManager.toggleTheme();
            themeBtn.setText(App.themeManager.getThemeIcon() + "  " + App.themeManager.getThemeName());
            db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.THEME_CHANGED,
                    "Switched to " + App.themeManager.getThemeName());
        });

        // User info card at bottom
        VBox userCard = buildUserCard();

        Button logoutBtn = createNavItem("🚪", "Logout", false);
        logoutBtn.setStyle(navStyle(false).replace("#E6EDF3", "#EF4444").replace("transparent", "transparent"));
        logoutBtn.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(logo, sep1, navItems, spacer, sep2,
                settingsBtn, aboutBtn, themeBtn, userCard, logoutBtn);
        return sidebar;
    }

    private Button createNavItem(String icon, String label, boolean active) {
        Button btn = new Button(icon + "  " + label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(navStyle(active));
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#6C63FF"))
                btn.setStyle(navStyle(true).replace("#6C63FF", "rgba(108,99,255,0.15)")
                        .replace("white", "#A78BFA"));
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#6C63FF") || btn.getStyle().contains("0.15"))
                btn.setStyle(navStyle(false));
        });
        return btn;
    }

    private String navStyle(boolean active) {
        return active
                ? "-fx-background-color: rgba(108,99,255,0.15); -fx-text-fill: white;" +
                "-fx-font-size:13px; -fx-background-radius:8; -fx-padding:10 12 10 12;" +
                "-fx-cursor:hand; -fx-font-family:'Segoe UI'; -fx-font-weight:600;" +
                "-fx-border-color: rgba(108,99,255,0.4); -fx-border-radius:8; -fx-border-width:0 0 0 2;"
                : "-fx-background-color:transparent; -fx-text-fill:#E6EDF3;" +
                "-fx-font-size:13px; -fx-background-radius:8; -fx-padding:10 12 10 12;" +
                "-fx-cursor:hand; -fx-font-family:'Segoe UI';";
    }

    private VBox buildUserCard() {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 10, 12, 10));
        card.setStyle(
                "-fx-background-color: rgba(108,99,255,0.08);" +
                        "-fx-background-radius:10; -fx-border-color:rgba(108,99,255,0.2);" +
                        "-fx-border-radius:10; -fx-border-width:1;"
        );

        Label name = new Label("👤 " + user.getFullName());
        name.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:12px; -fx-font-weight:600; -fx-text-fill:white;");

        Label role = new Label(user.isAdmin() ? "👑 Administrator" : "💬 User");
        role.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#6C63FF;");

        card.getChildren().addAll(name, role);
        return card;
    }

    // ─── MAIN CONTENT ────────────────────────────────────────────────────────

    private VBox buildMainContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(32, 36, 32, 36));
        content.setStyle("-fx-background-color: transparent;");

        // Header
        content.getChildren().add(buildHeader());

        // Stats row
        content.getChildren().add(buildStatsRow());

        // AI Status + Recent Conversations
        HBox midRow = new HBox(24);
        midRow.getChildren().addAll(buildAIStatusCard(), buildRecentConversations());
        HBox.setHgrow(buildAIStatusCard(), Priority.NEVER);
        content.getChildren().add(midRow);

        // Quick actions
        content.getChildren().add(buildQuickActions());

        return content;
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox leftText = new VBox(4);
        String greeting = getGreeting();
        Label greet = new Label(greeting + ", " + user.getFullName().split(" ")[0] + "! 👋");
        greet.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label sub = new Label("Here's your NeuraBot AI overview for today");
        sub.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-text-fill:#64748B;");

        leftText.getChildren().addAll(greet, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newChatBtn = new Button("💬  New Chat");
        newChatBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700;" +
                        "-fx-background-radius: 10; -fx-padding: 10 24 10 24;" +
                        "-fx-cursor: hand; -fx-font-family:'Segoe UI';"
        );
        newChatBtn.setOnAction(e -> showChat());

        header.getChildren().addAll(leftText, spacer, newChatBtn);
        return header;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);

        long totalUsers = analytics.getTotalUsers();
        long totalSessions = analytics.getTotalSessions();
        long totalMessages = analytics.getTotalMessages();
        long totalFAQs = analytics.getTotalFAQs();
        double satisfaction = analytics.getUserSatisfactionRate();

        String[][] stats = {
                {"💬", String.valueOf(totalSessions), "Total Sessions"},
                {"📨", String.valueOf(totalMessages), "Messages Sent"},
                {"📚", String.valueOf(totalFAQs), "Knowledge Articles"},
                {"😊", String.format("%.0f%%", satisfaction), "User Satisfaction"},
                {"👥", String.valueOf(totalUsers), "Registered Users"}
        };

        for (String[] stat : stats) {
            VBox card = buildStatCard(stat[0], stat[1], stat[2]);
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }

        return row;
    }

    private VBox buildStatCard(String icon, String value, String label) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20, 20, 20, 20));
        card.setMinHeight(100);
        card.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:22px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");

        // Animate value
        ScaleTransition st = new ScaleTransition(Duration.millis(600), valueLabel);
        st.setFromX(0.5);
        st.setToX(1);
        st.setFromY(0.5);
        st.setToY(1);
        st.play();

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#64748B;");

        card.getChildren().addAll(iconLabel, valueLabel, labelLabel);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(108,99,255,0.08);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(108,99,255,0.35);" +
                        "-fx-border-radius: 14; -fx-border-width: 1;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-radius: 14; -fx-border-width: 1;"
        ));

        return card;
    }

    private VBox buildAIStatusCard() {
        VBox card = new VBox(16);
        card.setPrefWidth(300);
        card.setMinWidth(300);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(34,197,94,0.3);" +
                        "-fx-border-radius: 14; -fx-border-width: 1;"
        );

        Label title = new Label("🤖 AI Status");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        // Online indicator
        HBox statusRow = new HBox(10);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill:#22C55E; -fx-font-size:14px;");

        FadeTransition blink = new FadeTransition(Duration.seconds(1.2), dot);
        blink.setFromValue(1); blink.setToValue(0.2); blink.setAutoReverse(true); blink.setCycleCount(-1); blink.play();

        Label statusText = new Label("NeuraBot AI  •  Online");
        statusText.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#22C55E;");
        statusRow.getChildren().addAll(dot, statusText);

        // Metrics
        String[][] metrics = {
                {"⚡ Response Speed", "< 50ms"},
                {"📚 Knowledge Size", KnowledgeBase.getInstance().getTotalFAQCount() + " articles"},
                {"🧠 NLP Engine", "Active"},
                {"💬 Your Sessions", String.valueOf(db.getSessionsByUser(user.getId()).size())},
                {"📨 Your Messages", String.valueOf(user.getTotalMessages())}
        };

        VBox metricsList = new VBox(10);
        for (String[] m : metrics) {
            HBox row = new HBox();
            Label mLabel = new Label(m[0]);
            mLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#64748B; -fx-font-family:'Segoe UI';");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label mValue = new Label(m[1]);
            mValue.setStyle("-fx-font-size:12px; -fx-text-fill:#A78BFA; -fx-font-weight:600; -fx-font-family:'Segoe UI';");
            row.getChildren().addAll(mLabel, sp, mValue);
            metricsList.getChildren().add(row);
        }

        card.getChildren().addAll(title, statusRow, new Separator(), metricsList);
        return card;
    }

    private VBox buildRecentConversations() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: #161B22;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-radius: 14; -fx-border-width: 1;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox cardHeader = new HBox();
        Label title = new Label("💬 Recent Conversations");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Hyperlink viewAll = new Hyperlink("View All");
        viewAll.setStyle("-fx-text-fill:#6C63FF; -fx-font-size:12px; -fx-border-color:transparent;");
        viewAll.setOnAction(e -> showChat());
        cardHeader.getChildren().addAll(title, sp, viewAll);

        VBox list = new VBox(8);
        List<ChatSession> sessions = db.getSessionsByUser(user.getId());

        if (sessions.isEmpty()) {
            Label empty = new Label("No conversations yet. Start chatting! 💬");
            empty.setStyle("-fx-text-fill:#374151; -fx-font-size:13px;");
            list.getChildren().add(empty);
        } else {
            int count = 0;
            for (ChatSession session : sessions) {
                if (count++ >= 5) break;
                HBox row = buildSessionRow(session);
                list.getChildren().add(row);
            }
        }

        card.getChildren().addAll(cardHeader, new Separator(), list);
        return card;
    }

    private HBox buildSessionRow(ChatSession session) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setStyle("-fx-background-color: rgba(108,99,255,0.04); -fx-background-radius:8;");

        Label icon = new Label("💬");
        icon.setStyle("-fx-font-size:18px;");

        VBox info = new VBox(2);
        Label sessionTitle = new Label(session.getTitle());
        sessionTitle.setStyle("-fx-font-size:13px; -fx-font-weight:600; -fx-text-fill:white; -fx-font-family:'Segoe UI';");
        sessionTitle.setMaxWidth(240);

        Label meta = new Label(session.getShortDate() + "  •  " + session.getMessageCount() + " messages");
        meta.setStyle("-fx-font-size:11px; -fx-text-fill:#374151; -fx-font-family:'Segoe UI';");
        info.getChildren().addAll(sessionTitle, meta);

        row.getChildren().addAll(icon, info);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(108,99,255,0.12); -fx-background-radius:8; -fx-cursor:hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: rgba(108,99,255,0.04); -fx-background-radius:8;"));
        return row;
    }

    private HBox buildQuickActions() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);

        String[][] actions = {
                {"💬", "Start New Chat", "Begin an AI conversation"},
                {"📚", "Knowledge Base", "Browse 100+ articles"},
                {"📊", "Analytics", "View insights & charts"},
                {"📋", "Generate Report", "Export your data"}
        };

        for (String[] action : actions) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(20));
            card.setPrefWidth(220);
            card.setStyle(
                    "-fx-background-color: #161B22;" +
                            "-fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.15);" +
                            "-fx-border-radius:14; -fx-border-width:1; -fx-cursor:hand;"
            );

            Label icon = new Label(action[0]);
            icon.setStyle("-fx-font-size:28px;");
            Label title = new Label(action[1]);
            title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-font-weight:700; -fx-text-fill:white;");
            Label desc = new Label(action[2]);
            desc.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#64748B;");

            card.getChildren().addAll(icon, title, desc);

            card.setOnMouseEntered(e -> card.setStyle(
                    "-fx-background-color:rgba(108,99,255,0.1);" +
                            "-fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.4);" +
                            "-fx-border-radius:14; -fx-border-width:1; -fx-cursor:hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.2), 12, 0, 0, 3);"
            ));
            card.setOnMouseExited(e -> card.setStyle(
                    "-fx-background-color: #161B22;" +
                            "-fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.15);" +
                            "-fx-border-radius:14; -fx-border-width:1; -fx-cursor:hand;"
            ));

            final int idx = java.util.Arrays.asList(actions).indexOf(action);
            card.setOnMouseClicked(e -> {
                switch (idx) {
                    case 0 -> showChat();
                    case 1 -> showKnowledgeBase();
                    case 2 -> showAnalytics();
                    case 3 -> {
                        notif.info("Report generation opened in Reports section.");
                        showAnalytics();
                    }
                }
            });

            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }

        return row;
    }

    // ─── NAVIGATION ──────────────────────────────────────────────────────────

    private void showChat() {
        ChatInterface chat = new ChatInterface(stage, user);
        chat.show();
    }

    private void showKnowledgeBase() {
        KnowledgeBaseView kb = new KnowledgeBaseView(stage, user);
        kb.show();
    }

    private void showAnalytics() {
        AnalyticsView av = new AnalyticsView(stage, user);
        av.show();
    }

    private void showAdminPanel() {
        AdminPanel admin = new AdminPanel(stage, user);
        admin.show();
    }

    private void showSettings() {
        SettingsView settings = new SettingsView(stage, user);
        settings.show();
    }

    private void showAbout() {
        AboutView about = new AboutView(stage, user);
        about.show();
    }

    private void handleLogout() {
        db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.LOGOUT, "User logged out");
        LoginScreen login = new LoginScreen(stage);
        login.show();
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    }
}
