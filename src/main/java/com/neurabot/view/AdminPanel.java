package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.database.DatabaseManager;
import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.*;
import com.neurabot.util.NotificationManager;
import javafx.animation.FadeTransition;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

/**
 * Admin control panel: user management, FAQ training, activity logs.
 */
public class AdminPanel {

    private final Stage stage;
    private final User user;
    private final DatabaseManager db;
    private final KnowledgeBase kb;
    private final NotificationManager notif;

    private TabPane tabPane;

    public AdminPanel(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.db = DatabaseManager.getInstance();
        this.kb = KnowledgeBase.getInstance();
        this.notif = new NotificationManager(stage);
    }

    public void show() {
        if (!user.isAdmin()) {
            notif.error("Access denied. Admin privileges required.");
            return;
        }

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

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:#161B22; -fx-border-color:rgba(239,68,68,0.2); -fx-border-width:0 1 0 0;");

        Label adminBadge = new Label("👑 ADMIN PANEL");
        adminBadge.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:12px; -fx-font-weight:900; -fx-text-fill:#EF4444; -fx-padding:4 0 8 4;");

        Button backBtn = new Button("← Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-background-color:rgba(108,99,255,0.1); -fx-text-fill:#A78BFA; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
        backBtn.setOnAction(e -> goBack());

        // Quick stats
        VBox quickStats = new VBox(8);
        quickStats.setPadding(new Insets(12, 8, 12, 8));
        quickStats.setStyle("-fx-background-color:rgba(239,68,68,0.05); -fx-background-radius:10; -fx-border-color:rgba(239,68,68,0.15); -fx-border-radius:10; -fx-border-width:1;");

        Label statsTitle = new Label("System Overview");
        statsTitle.setStyle("-fx-font-size:11px; -fx-text-fill:#EF4444; -fx-font-weight:600;");

        addStatRow(quickStats, "👥 Total Users", String.valueOf(db.getTotalUsers()));
        addStatRow(quickStats, "💬 Sessions", String.valueOf(db.getTotalSessions()));
        addStatRow(quickStats, "📚 FAQs", String.valueOf(kb.getTotalFAQCount()));
        addStatRow(quickStats, "📋 Log Entries", String.valueOf(db.getActivityLogs().size()));
        quickStats.getChildren().add(0, statsTitle);

        sidebar.getChildren().addAll(adminBadge, backBtn, new Separator(), quickStats);
        return sidebar;
    }

    private void addStatRow(VBox parent, String label, String value) {
        HBox row = new HBox();
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-font-size:11px; -fx-text-fill:#A78BFA; -fx-font-weight:700;");
        row.getChildren().addAll(lbl, sp, val);
        parent.getChildren().add(row);
    }

    // ─── MAIN CONTENT ────────────────────────────────────────────────────────

    private VBox buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 28, 24, 28));

        // Header
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("👑");
        icon.setStyle("-fx-font-size:28px;");
        VBox titleBox = new VBox(2);
        Label title = new Label("Admin Control Panel");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Manage users, FAQs, chatbot training, and system logs");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");
        titleBox.getChildren().addAll(title, sub);
        header.getChildren().addAll(icon, titleBox);

        // Tabs
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color:transparent; -fx-tab-min-width:120;");
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        tabPane.getTabs().addAll(
                buildUsersTab(),
                buildTrainBotTab(),
                buildLogsTab()
        );

        content.getChildren().addAll(header, tabPane);
        return content;
    }

    // ─── USERS TAB ───────────────────────────────────────────────────────────

    private Tab buildUsersTab() {
        Tab tab = new Tab("👥  Users");
        tab.setClosable(false);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label subTitle = new Label("All registered users in the system");
        subTitle.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");

        // User table
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color:#161B22; -fx-background-radius:10;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setStyle("-fx-text-fill:white;");

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Integer> sessionsCol = new TableColumn<>("Sessions");
        sessionsCol.setCellValueFactory(new PropertyValueFactory<>("totalSessions"));

        TableColumn<User, Integer> msgsCol = new TableColumn<>("Messages");
        msgsCol.setCellValueFactory(new PropertyValueFactory<>("totalMessages"));

        TableColumn<User, String> lastLoginCol = new TableColumn<>("Last Login");
        lastLoginCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedLastLogin()));

        table.getColumns().addAll(nameCol, usernameCol, emailCol, roleCol, sessionsCol, msgsCol, lastLoginCol);
        table.getItems().addAll(db.getAllUsers());

        // Action buttons
        HBox actions = new HBox(10);
        Button refreshBtn = adminActionBtn("🔄 Refresh", "#6C63FF");
        refreshBtn.setOnAction(e -> {
            table.getItems().clear();
            table.getItems().addAll(db.getAllUsers());
            notif.info("User list refreshed.");
        });

        Button promoteBtn = adminActionBtn("👑 Toggle Admin", "#F59E0B");
        promoteBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { notif.warning("Please select a user first."); return; }
            if (selected.getId().equals(user.getId())) { notif.warning("Cannot change your own role."); return; }
            selected.setRole(selected.isAdmin() ? "user" : "admin");
            db.updateUser(selected);
            table.refresh();
            notif.success("Role updated for: " + selected.getUsername());
        });

        Button deleteBtn = adminActionBtn("🗑 Delete User", "#EF4444");
        deleteBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { notif.warning("Please select a user first."); return; }
            if (selected.getId().equals(user.getId())) { notif.warning("Cannot delete yourself."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete user: " + selected.getUsername() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    db.deleteUser(selected.getId());
                    table.getItems().remove(selected);
                    notif.success("User deleted.");
                }
            });
        });

        actions.getChildren().addAll(refreshBtn, promoteBtn, deleteBtn);

        content.getChildren().addAll(subTitle, actions, table);
        tab.setContent(content);
        return tab;
    }

    // ─── TRAIN BOT TAB ───────────────────────────────────────────────────────

    private Tab buildTrainBotTab() {
        Tab tab = new Tab("🧠  Train Chatbot");
        tab.setClosable(false);

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        Label info = new Label("Train the chatbot with new question-answer pairs. These are added directly to the knowledge base.");
        info.setStyle("-fx-text-fill:#64748B; -fx-font-size:13px; -fx-wrap-text:true;");
        info.setWrapText(true);

        // Training form
        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color:#161B22; -fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.15); -fx-border-radius:14; -fx-border-width:1;");

        Label formTitle = new Label("Add New Training Data");
        formTitle.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        TextField questionField = createTrainField("User Question (what the user might ask)");
        TextArea answerField = new TextArea();
        answerField.setPromptText("Bot's answer (what NeuraBot should reply)");
        answerField.setPrefRowCount(4);
        answerField.setStyle("-fx-background-color:#0D1117; -fx-text-fill:white; -fx-prompt-text-fill:#374151; -fx-background-radius:10; -fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:10; -fx-border-width:1; -fx-padding:10 14; -fx-font-size:13px;");

        TextField categoryField = createTrainField("Category (e.g., AI, Java, Programming)");
        TextField keywordsField = createTrainField("Keywords (comma-separated, e.g., java,oop,class)");

        Button trainBtn = new Button("🧠  Train NeuraBot");
        trainBtn.setStyle("-fx-background-color:linear-gradient(to right,#6C63FF,#8B5CF6); -fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:700; -fx-background-radius:10; -fx-padding:12 28; -fx-cursor:hand;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size:13px;");

        trainBtn.setOnAction(e -> {
            String q = questionField.getText().trim();
            String a = answerField.getText().trim();
            String cat = categoryField.getText().trim();
            String[] kws = keywordsField.getText().split(",");

            if (q.isEmpty() || a.isEmpty() || cat.isEmpty()) {
                statusLabel.setText("⚠  Question, Answer, and Category are required.");
                statusLabel.setStyle("-fx-text-fill:#EF4444; -fx-font-size:13px;");
                return;
            }

            FAQ faq = new FAQ(q, a, cat, kws);
            faq.setLearned(true);
            kb.addFAQ(faq);

            db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.KNOWLEDGE_TRAINED,
                    "Trained: " + q);

            questionField.clear();
            answerField.clear();
            categoryField.clear();
            keywordsField.clear();

            statusLabel.setText("✅  Chatbot trained successfully! New FAQ added to knowledge base.");
            statusLabel.setStyle("-fx-text-fill:#22C55E; -fx-font-size:13px;");
            notif.success("✅ Chatbot trained with new knowledge!");

            // Refresh after 2 seconds
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ev -> statusLabel.setText(""));
            pause.play();
        });

        form.getChildren().addAll(formTitle, new Label("Question:") {{setStyle("-fx-text-fill:#64748B; -fx-font-size:12px;");}},
                questionField, new Label("Answer:") {{setStyle("-fx-text-fill:#64748B; -fx-font-size:12px;");}},
                answerField, new Label("Category:") {{setStyle("-fx-text-fill:#64748B; -fx-font-size:12px;");}},
                categoryField, new Label("Keywords:") {{setStyle("-fx-text-fill:#64748B; -fx-font-size:12px;");}},
                keywordsField, trainBtn, statusLabel);

        // Learned FAQs table
        Label learnedTitle = new Label("🧪 Trained Entries");
        learnedTitle.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:white;");

        TableView<FAQ> learnedTable = new TableView<>();
        learnedTable.setStyle("-fx-background-color:#161B22;");
        learnedTable.setMaxHeight(200);

        TableColumn<FAQ, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<FAQ, String> qCol = new TableColumn<>("Question");
        qCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        TableColumn<FAQ, Integer> hitCol = new TableColumn<>("Hits");
        hitCol.setCellValueFactory(new PropertyValueFactory<>("hitCount"));

        learnedTable.getColumns().addAll(catCol, qCol, hitCol);
        learnedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        kb.getAllFAQs().stream().filter(FAQ::isLearned).forEach(learnedTable.getItems()::add);

        content.getChildren().addAll(info, form, learnedTitle, learnedTable);
        tab.setContent(new ScrollPane(content) {{
            setStyle("-fx-background:transparent; -fx-background-color:transparent;");
            setFitToWidth(true);
        }});
        return tab;
    }

    // ─── LOGS TAB ────────────────────────────────────────────────────────────

    private Tab buildLogsTab() {
        Tab tab = new Tab("📋  Activity Logs");
        tab.setClosable(false);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label sub = new Label("System activity log — all user actions and events");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search logs...");
        searchField.setStyle("-fx-background-color:#161B22; -fx-text-fill:white; -fx-prompt-text-fill:#374151; -fx-background-radius:8; -fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:8; -fx-border-width:1; -fx-padding:8 12; -fx-font-size:13px;");

        TableView<ActivityLog> logTable = new TableView<>();
        logTable.setStyle("-fx-background-color:#161B22;");
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(logTable, Priority.ALWAYS);

        TableColumn<ActivityLog, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedTimestamp()));
        timeCol.setMinWidth(160);

        TableColumn<ActivityLog, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<ActivityLog, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIcon() + " " + data.getValue().getLogType()));

        TableColumn<ActivityLog, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        logTable.getColumns().addAll(timeCol, userCol, typeCol, descCol);

        List<ActivityLog> logs = db.getActivityLogs();
        logTable.getItems().addAll(logs);

        // Search filter
        searchField.textProperty().addListener((obs, old, query) -> {
            logTable.getItems().clear();
            String lower = query.toLowerCase();
            logs.stream()
                    .filter(l -> l.getDescription().toLowerCase().contains(lower)
                            || l.getUsername().toLowerCase().contains(lower))
                    .forEach(logTable.getItems()::add);
        });

        Button clearLogsBtn = adminActionBtn("🗑 Clear Logs", "#EF4444");
        clearLogsBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Clear all activity logs?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    logTable.getItems().clear();
                    notif.warning("Activity logs cleared.");
                }
            });
        });

        content.getChildren().addAll(sub, searchField, clearLogsBtn, logTable);
        tab.setContent(content);
        return tab;
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private TextField createTrainField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(42);
        field.setStyle("-fx-background-color:#0D1117; -fx-text-fill:white; -fx-prompt-text-fill:#374151; -fx-background-radius:10; -fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:10; -fx-border-width:1; -fx-padding:0 14; -fx-font-size:13px;");
        return field;
    }

    private Button adminActionBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + color + "22; -fx-text-fill:" + color + "; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 16; -fx-cursor:hand; -fx-border-color:" + color + "44; -fx-border-radius:8; -fx-border-width:1;");
        return btn;
    }

    private void goBack() {
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }
}
