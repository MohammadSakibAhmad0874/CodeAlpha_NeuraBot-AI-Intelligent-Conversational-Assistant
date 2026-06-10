package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.ai.ChatbotEngine;
import com.neurabot.ai.RecommendationEngine;
import com.neurabot.database.DatabaseManager;
import com.neurabot.model.*;
import com.neurabot.util.NotificationManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main AI chat interface — the heart of NeuraBot.
 * Features message bubbles, typing indicator, suggestions, and rich history sidebar.
 */
public class ChatInterface {

    private final Stage stage;
    private final User user;
    private final DatabaseManager db;
    private final ChatbotEngine engine;
    private final RecommendationEngine recommendationEngine;
    private final NotificationManager notif;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ChatSession currentSession;
    private VBox messageContainer;
    private ScrollPane messageScroll;
    private TextField inputField;
    private HBox typingIndicator;
    private VBox sessionList;

    public ChatInterface(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.db = DatabaseManager.getInstance();
        this.engine = new ChatbotEngine();
        this.engine.setPersonality(user.getAiPersonality());
        this.recommendationEngine = new RecommendationEngine();
        this.notif = new NotificationManager(stage);
        this.currentSession = new ChatSession(user.getId());
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1117;");

        // LEFT: Chat history sidebar
        VBox chatSidebar = buildChatSidebar();
        root.setLeft(chatSidebar);

        // CENTER: Chat area
        root.setCenter(buildChatArea());

        Scene scene = new Scene(root, 1280, 800);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        // Welcome bot message
        Platform.runLater(() -> {
            sendBotMessage("👋 Hello, " + user.getFullName().split(" ")[0] + "! I'm **NeuraBot AI**.\n\n" +
                    "I'm your intelligent virtual assistant, ready to help with:\n" +
                    "🧠 AI & Machine Learning\n☕ Java Programming\n📊 Data Structures\n" +
                    "🌐 Web Development\n💡 General Knowledge\n\n" +
                    "What would you like to explore today?");
            inputField.requestFocus();
        });

        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(0); fade.setToValue(1); fade.play();
    }

    // ─── CHAT SIDEBAR ────────────────────────────────────────────────────────

    private VBox buildChatSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(16));
        sidebar.setStyle("-fx-background-color: #161B22; -fx-border-color:rgba(108,99,255,0.15); -fx-border-width:0 1 0 0;");

        // Back button
        Button backBtn = new Button("← Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle(
                "-fx-background-color:rgba(108,99,255,0.1); -fx-text-fill:#A78BFA;" +
                        "-fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 12;" +
                        "-fx-cursor:hand; -fx-font-family:'Segoe UI';"
        );
        backBtn.setOnAction(e -> goToDashboard());

        // New Chat button
        Button newChatBtn = new Button("+ New Chat");
        newChatBtn.setMaxWidth(Double.MAX_VALUE);
        newChatBtn.setStyle(
                "-fx-background-color:linear-gradient(to right,#6C63FF,#8B5CF6);" +
                        "-fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:700;" +
                        "-fx-background-radius:8; -fx-padding:9 12; -fx-cursor:hand; -fx-font-family:'Segoe UI';"
        );
        newChatBtn.setOnAction(e -> startNewSession());

        Label historyLabel = new Label("Recent Chats");
        historyLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#374151; -fx-font-weight:600;");

        sessionList = new VBox(4);
        ScrollPane sessionScroll = new ScrollPane(sessionList);
        sessionScroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        sessionScroll.setFitToWidth(true);
        sessionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(sessionScroll, Priority.ALWAYS);

        refreshSessionList();

        sidebar.getChildren().addAll(backBtn, newChatBtn, new Separator(), historyLabel, sessionScroll);
        return sidebar;
    }

    private void refreshSessionList() {
        sessionList.getChildren().clear();
        List<ChatSession> sessions = db.getSessionsByUser(user.getId());
        for (ChatSession session : sessions) {
            VBox sessionItem = new VBox(2);
            sessionItem.setPadding(new Insets(8, 10, 8, 10));
            sessionItem.setStyle("-fx-background-color: rgba(108,99,255,0.04); -fx-background-radius:8; -fx-cursor:hand;");

            Label title = new Label(session.getTitle());
            title.setStyle("-fx-font-size:12px; -fx-text-fill:#94A3B8; -fx-font-family:'Segoe UI';");
            title.setMaxWidth(190);

            Label date = new Label(session.getShortDate() + " · " + session.getMessageCount() + " msgs");
            date.setStyle("-fx-font-size:10px; -fx-text-fill:#374151; -fx-font-family:'Segoe UI';");

            sessionItem.getChildren().addAll(title, date);
            sessionItem.setOnMouseEntered(e -> sessionItem.setStyle("-fx-background-color:rgba(108,99,255,0.12); -fx-background-radius:8; -fx-cursor:hand;"));
            sessionItem.setOnMouseExited(e -> sessionItem.setStyle("-fx-background-color:rgba(108,99,255,0.04); -fx-background-radius:8;"));
            sessionList.getChildren().add(sessionItem);
        }
    }

    // ─── CHAT AREA ───────────────────────────────────────────────────────────

    private BorderPane buildChatArea() {
        BorderPane area = new BorderPane();
        area.setStyle("-fx-background-color:#0D1117;");

        // Top bar
        area.setTop(buildChatTopBar());

        // Message container
        messageContainer = new VBox(12);
        messageContainer.setPadding(new Insets(20, 24, 20, 24));
        messageContainer.setStyle("-fx-background-color:#0D1117;");

        // Typing indicator (hidden by default)
        typingIndicator = buildTypingIndicator();
        typingIndicator.setVisible(false);
        typingIndicator.setManaged(false);
        messageContainer.getChildren().add(typingIndicator);

        messageScroll = new ScrollPane(messageContainer);
        messageScroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        messageScroll.setFitToWidth(true);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.vvalueProperty().bind(messageContainer.heightProperty());
        area.setCenter(messageScroll);

        // Bottom: suggestions + input
        VBox bottomBar = new VBox(0);
        bottomBar.getChildren().addAll(buildSuggestionBar(), buildInputBar());
        area.setBottom(bottomBar);

        return area;
    }

    private HBox buildChatTopBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle(
                "-fx-background-color:#161B22;" +
                        "-fx-border-color:rgba(108,99,255,0.15); -fx-border-width:0 0 1 0;"
        );

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill:#22C55E; -fx-font-size:12px;");
        FadeTransition blink = new FadeTransition(Duration.seconds(1.5), dot);
        blink.setFromValue(1); blink.setToValue(0.3); blink.setAutoReverse(true); blink.setCycleCount(-1); blink.play();

        Label botName = new Label("NeuraBot AI");
        botName.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label status = new Label("• Online  |  AI Assistant");
        status.setStyle("-fx-font-size:12px; -fx-text-fill:#64748B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button clearBtn = new Button("🗑 Clear Chat");
        clearBtn.setStyle("-fx-background-color:rgba(239,68,68,0.1); -fx-text-fill:#EF4444; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:6 12; -fx-cursor:hand;");
        clearBtn.setOnAction(e -> clearChat());

        bar.getChildren().addAll(dot, botName, status, spacer, clearBtn);
        return bar;
    }

    private HBox buildSuggestionBar() {
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(10, 20, 4, 20));
        bar.setStyle("-fx-background-color:#0D1117;");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Try asking:");
        label.setStyle("-fx-font-size:11px; -fx-text-fill:#374151; -fx-font-family:'Segoe UI';");

        List<String> suggestions = recommendationEngine.getDefaultSuggestions();
        bar.getChildren().add(label);
        for (String suggestion : suggestions) {
            Button chip = new Button(suggestion);
            chip.setStyle(
                    "-fx-background-color:rgba(108,99,255,0.08);" +
                            "-fx-text-fill:#A78BFA; -fx-font-size:11px;" +
                            "-fx-background-radius:20; -fx-padding:4 12; -fx-cursor:hand;" +
                            "-fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:20;"
            );
            chip.setOnAction(e -> {
                // Remove emoji prefix
                String query = suggestion.replaceAll("^[\\p{So}\\p{Sm}\\p{Sk}\\p{Sc}\\p{Po}\\s]+", "").trim();
                if (query.startsWith("🤖") || query.startsWith("🧠") || query.startsWith("☕") || query.startsWith("🔧") || query.startsWith("💡")) {
                    query = query.substring(2).trim();
                }
                inputField.setText(query);
                sendUserMessage();
            });
            chip.setOnMouseEntered(ev -> chip.setStyle(
                    "-fx-background-color:rgba(108,99,255,0.18); -fx-text-fill:white;" +
                            "-fx-font-size:11px; -fx-background-radius:20; -fx-padding:4 12; -fx-cursor:hand;" +
                            "-fx-border-color:rgba(108,99,255,0.4); -fx-border-radius:20;"
            ));
            chip.setOnMouseExited(ev -> chip.setStyle(
                    "-fx-background-color:rgba(108,99,255,0.08); -fx-text-fill:#A78BFA;" +
                            "-fx-font-size:11px; -fx-background-radius:20; -fx-padding:4 12; -fx-cursor:hand;" +
                            "-fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:20;"
            ));
            bar.getChildren().add(chip);
        }

        return bar;
    }

    private HBox buildInputBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(12, 20, 16, 20));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle(
                "-fx-background-color:#161B22;" +
                        "-fx-border-color:rgba(108,99,255,0.2); -fx-border-width:1 0 0 0;"
        );

        inputField = new TextField();
        inputField.setPromptText("Ask NeuraBot AI anything...");
        inputField.setPrefHeight(46);
        inputField.setStyle(
                "-fx-background-color:#0D1117; -fx-text-fill:white;" +
                        "-fx-prompt-text-fill:#374151;" +
                        "-fx-background-radius:12; -fx-border-color:rgba(108,99,255,0.25);" +
                        "-fx-border-radius:12; -fx-border-width:1;" +
                        "-fx-padding:0 16 0 16; -fx-font-size:14px; -fx-font-family:'Segoe UI';"
        );
        inputField.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                inputField.setStyle(inputField.getStyle()
                        .replace("rgba(108,99,255,0.25)", "#6C63FF"));
            } else {
                inputField.setStyle(inputField.getStyle()
                        .replace("#6C63FF", "rgba(108,99,255,0.25)"));
            }
        });
        HBox.setHgrow(inputField, Priority.ALWAYS);

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendUserMessage();
        });

        Button sendBtn = new Button("Send ↑");
        sendBtn.setPrefHeight(46);
        sendBtn.setPrefWidth(90);
        sendBtn.setStyle(
                "-fx-background-color:linear-gradient(to right,#6C63FF,#8B5CF6);" +
                        "-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:700;" +
                        "-fx-background-radius:12; -fx-cursor:hand; -fx-font-family:'Segoe UI';"
        );
        sendBtn.setOnAction(e -> sendUserMessage());

        bar.getChildren().addAll(inputField, sendBtn);
        return bar;
    }

    // ─── MESSAGING ───────────────────────────────────────────────────────────

    private void sendUserMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        inputField.clear();

        // Add user bubble
        Message userMsg = new Message(currentSession.getId(), text, Message.Sender.USER);
        currentSession.addMessage(userMsg);
        addMessageBubble(userMsg);
        db.incrementUserMessages(user.getId());

        // Show typing indicator
        showTypingIndicator(true);

        // Process asynchronously
        executor.submit(() -> {
            try {
                Thread.sleep(600 + (long)(Math.random() * 800));
            } catch (InterruptedException ignored) {}

            Message response = engine.processMessage(text, user);
            currentSession.addMessage(response);

            Platform.runLater(() -> {
                showTypingIndicator(false);
                addMessageBubble(response);
                db.saveSession(currentSession);
                refreshSessionList();
            });
        });
    }

    private void sendBotMessage(String text) {
        Message msg = new Message(currentSession.getId(), text, Message.Sender.BOT);
        addMessageBubble(msg);
    }

    private void addMessageBubble(Message message) {
        HBox wrapper = new HBox();
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.setPadding(new Insets(2, 0, 2, 0));

        boolean isUser = message.isFromUser();
        wrapper.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox(4);
        bubble.setMaxWidth(620);
        bubble.setPadding(new Insets(12, 16, 12, 16));

        if (isUser) {
            bubble.setStyle(
                    "-fx-background-color:linear-gradient(to bottom right,#6C63FF,#8B5CF6);" +
                            "-fx-background-radius:18 18 4 18;"
            );
        } else {
            bubble.setStyle(
                    "-fx-background-color:#161B22;" +
                            "-fx-background-radius:18 18 18 4;" +
                            "-fx-border-color:rgba(108,99,255,0.15);" +
                            "-fx-border-radius:18 18 18 4; -fx-border-width:1;"
            );
        }

        // Bot prefix
        if (!isUser) {
            Label prefix = new Label("🤖 NeuraBot");
            prefix.setStyle("-fx-font-size:11px; -fx-text-fill:#6C63FF; -fx-font-weight:600; -fx-font-family:'Segoe UI';");
            bubble.getChildren().add(prefix);
        }

        // Content label — handle markdown-like formatting
        Label content = new Label(formatMessage(message.getContent()));
        content.setWrapText(true);
        content.setMaxWidth(590);
        content.setStyle(
                "-fx-font-size:14px; -fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + (isUser ? "white" : "#E6EDF3") + ";" +
                        "-fx-line-spacing: 3;"
        );
        bubble.getChildren().add(content);

        // Timestamp
        Label timestamp = new Label(message.getFormattedTime());
        timestamp.setStyle("-fx-font-size:10px; -fx-text-fill:" + (isUser ? "rgba(255,255,255,0.5)" : "#374151") + ";");
        timestamp.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.getChildren().add(timestamp);

        wrapper.getChildren().add(bubble);

        // Animate bubble
        FadeTransition ft = new FadeTransition(Duration.millis(300), bubble);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), bubble);
        tt.setFromY(10); tt.setToY(0); tt.play();

        // Insert before typing indicator
        int insertIdx = messageContainer.getChildren().indexOf(typingIndicator);
        if (insertIdx >= 0) {
            messageContainer.getChildren().add(insertIdx, wrapper);
        } else {
            messageContainer.getChildren().add(wrapper);
        }
    }

    private HBox buildTypingIndicator() {
        HBox wrapper = new HBox(10);
        wrapper.setAlignment(Pos.CENTER_LEFT);

        VBox bubble = new VBox(6);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle("-fx-background-color:#161B22; -fx-background-radius:18; -fx-border-color:rgba(108,99,255,0.15); -fx-border-radius:18; -fx-border-width:1;");

        Label label = new Label("NeuraBot is thinking");
        label.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B; -fx-font-family:'Segoe UI';");

        HBox dots = new HBox(4);
        dots.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 3; i++) {
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill:#6C63FF; -fx-font-size:8px;");
            ScaleTransition bounce = new ScaleTransition(Duration.millis(400), dot);
            bounce.setFromY(0.5); bounce.setToY(1.4);
            bounce.setAutoReverse(true); bounce.setCycleCount(-1);
            bounce.setDelay(Duration.millis(i * 130));
            bounce.play();
            dots.getChildren().add(dot);
        }

        bubble.getChildren().addAll(label, dots);
        wrapper.getChildren().add(bubble);
        return wrapper;
    }

    private void showTypingIndicator(boolean show) {
        typingIndicator.setVisible(show);
        typingIndicator.setManaged(show);
    }

    private void clearChat() {
        messageContainer.getChildren().clear();
        messageContainer.getChildren().add(typingIndicator);
        currentSession = new ChatSession(user.getId());
        engine.clearHistory();
        sendBotMessage("Chat cleared! 🧹 How can I help you?");
    }

    private void startNewSession() {
        if (currentSession.getMessageCount() > 0) {
            currentSession.endSession();
            db.saveSession(currentSession);
        }
        currentSession = new ChatSession(user.getId());
        engine.clearHistory();
        messageContainer.getChildren().clear();
        messageContainer.getChildren().add(typingIndicator);
        sendBotMessage("New conversation started! 🆕 What would you like to discuss?");
        refreshSessionList();
        notif.success("New chat session started!");
    }

    private void goToDashboard() {
        if (currentSession.getMessageCount() > 0) {
            currentSession.endSession();
            db.saveSession(currentSession);
        }
        executor.shutdown();
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }

    /**
     * Very basic markdown-like formatting for bot messages.
     * Strips **bold** markers for clean display.
     */
    private String formatMessage(String text) {
        return text.replaceAll("\\*\\*(.*?)\\*\\*", "$1");
    }
}
