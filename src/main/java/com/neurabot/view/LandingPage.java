package com.neurabot.view;

import com.neurabot.App;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 * Premium landing/marketing page inspired by ChatGPT and Gemini.
 * Features animated particles, feature cards, and statistics counters.
 */
public class LandingPage {

    private final Stage stage;
    private final Random random = new Random();
    private AnimationTimer particleTimer;

    private static final int PARTICLE_COUNT = 80;
    private double[] px, py, pvx, pvy, pr;
    private double W, H;

    public LandingPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setResizable(true);
        stage.centerOnScreen();

        W = 1280;
        H = 800;

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0D1117;");

        // Background canvas
        Canvas canvas = new Canvas(W, H);
        initParticles();
        startParticleAnimation(canvas.getGraphicsContext2D());
        root.getChildren().add(canvas);

        // Make canvas resize with window
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        // Main scroll content
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox mainContent = new VBox(0);
        mainContent.setStyle("-fx-background-color: transparent;");
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.getChildren().addAll(
                buildNavBar(),
                buildHeroSection(),
                buildFeaturesSection(),
                buildStatsSection(),
                buildCTASection(),
                buildFooter()
        );

        scroll.setContent(mainContent);
        root.getChildren().add(scroll);

        Scene scene = new Scene(root, 1280, 800);
        App.themeManager.registerScene(scene);
        stage.setScene(scene);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // ─── NAVBAR ──────────────────────────────────────────────────────────────

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(18, 60, 18, 60));
        nav.setStyle(
                "-fx-background-color: rgba(13,17,23,0.85);" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Logo
        HBox logo = new HBox(10);
        logo.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("🤖");
        logoIcon.setStyle("-fx-font-size: 24px;");
        Label logoText = new Label("NeuraBot AI");
        logoText.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#6C63FF;");
        logo.getChildren().addAll(logoIcon, logoText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Nav buttons
        Button loginBtn = createNavButton("Login", false);
        loginBtn.setOnAction(e -> showLogin());

        Button startBtn = createNavButton("Start Free", true);
        startBtn.setOnAction(e -> showLogin());

        HBox navRight = new HBox(12, loginBtn, startBtn);
        navRight.setAlignment(Pos.CENTER_RIGHT);

        nav.getChildren().addAll(logo, spacer, navRight);
        return nav;
    }

    // ─── HERO SECTION ────────────────────────────────────────────────────────

    private VBox buildHeroSection() {
        VBox hero = new VBox(28);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(90, 60, 80, 60));
        hero.setStyle("-fx-background-color: transparent;");
        hero.setMinHeight(520);

        // Badge
        Label badge = new Label("✨  AI-Powered Conversational Intelligence");
        badge.setStyle(
                "-fx-background-color: rgba(108,99,255,0.12);" +
                        "-fx-border-color: rgba(108,99,255,0.3);" +
                        "-fx-border-radius: 20; -fx-background-radius:20;" +
                        "-fx-text-fill: #A78BFA; -fx-font-size:12px;" +
                        "-fx-padding: 6 16 6 16; -fx-font-family:'Segoe UI';"
        );

        // Main headline
        Label headline = new Label("NeuraBot AI");
        headline.setStyle(
                "-fx-font-family:'Segoe UI';" +
                        "-fx-font-size:76px;" +
                        "-fx-font-weight:900;" +
                        "-fx-text-fill:white;"
        );

        Label subHeadline = new Label("Think.  Learn.  Assist.");
        subHeadline.setStyle(
                "-fx-font-family:'Segoe UI';" +
                        "-fx-font-size:22px;" +
                        "-fx-text-fill: #6C63FF;" +
                        "-fx-font-weight: 600;" +
                        "-fx-letter-spacing: 3;"
        );

        Label description = new Label(
                "Your Intelligent Virtual Assistant Powered by Artificial Intelligence\n" +
                        "Experience smart conversations, real-time insights, and AI-driven recommendations."
        );
        description.setStyle(
                "-fx-font-family:'Segoe UI'; -fx-font-size:16px;" +
                        "-fx-text-fill:#94A3B8; -fx-text-alignment:center; -fx-alignment:center;"
        );
        description.setWrapText(true);
        description.setMaxWidth(600);
        description.setTextAlignment(TextAlignment.CENTER);

        // CTA Buttons
        HBox buttons = new HBox(16);
        buttons.setAlignment(Pos.CENTER);

        Button startBtn = createHeroButton("🚀  Start Chatting", true);
        startBtn.setOnAction(e -> showLogin());

        Button featuresBtn = createHeroButton("✨  Explore Features", false);
        featuresBtn.setOnAction(e -> {
            /* scroll down */ });

        buttons.getChildren().addAll(startBtn, featuresBtn);

        // Animated headline
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), headline);
        pulse.setFromX(0.95);
        pulse.setToX(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        hero.getChildren().addAll(badge, headline, subHeadline, description, buttons);

        // Slide in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(700), hero);
        slideIn.setFromY(40);
        slideIn.setToY(0);
        slideIn.play();

        return hero;
    }

    // ─── FEATURES SECTION ────────────────────────────────────────────────────

    private VBox buildFeaturesSection() {
        VBox section = new VBox(40);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(70, 60, 80, 60));
        section.setStyle("-fx-background-color: rgba(22,27,34,0.8);");

        Label title = new Label("Powered by Advanced AI Technology");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:34px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label sub = new Label("Everything you need for intelligent conversational AI in one platform");
        sub.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:15px; -fx-text-fill:#64748B;");

        // Feature grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        String[][] features = {
                {"🧠", "Natural Language Processing", "Advanced tokenization, intent detection, and keyword extraction for true language understanding"},
                {"💬", "Smart Conversations", "Context-aware responses that remember your conversation history and adapt to your needs"},
                {"📚", "Knowledge Base", "100+ curated articles across AI, Java, algorithms, databases, and more topics"},
                {"💡", "AI Recommendations", "Intelligent topic suggestions based on your interests and conversation patterns"},
                {"🎤", "Voice Interaction", "Speech-to-text and text-to-speech capabilities for hands-free assistance"},
                {"📊", "Analytics Dashboard", "Real-time conversation analytics with charts, insights, and performance metrics"}
        };

        for (int i = 0; i < features.length; i++) {
            VBox card = createFeatureCard(features[i][0], features[i][1], features[i][2]);
            grid.add(card, i % 3, i / 3);
        }

        section.getChildren().addAll(title, sub, grid);
        return section;
    }

    private VBox createFeatureCard(String icon, String title, String desc) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(28));
        card.setPrefWidth(360);
        card.setMinHeight(170);
        card.setStyle(
                "-fx-background-color: rgba(22,27,34,0.9);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.08), 12, 0, 0, 2);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");
        titleLabel.setWrapText(true);

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#64748B;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(108,99,255,0.08);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(108,99,255,0.4);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.2), 20, 0, 0, 4);" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(22,27,34,0.9);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(108,99,255,0.15);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.08), 12, 0, 0, 2);"
        ));

        return card;
    }

    // ─── STATS SECTION ───────────────────────────────────────────────────────

    private HBox buildStatsSection() {
        HBox stats = new HBox(0);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(60, 60, 60, 60));
        stats.setStyle("-fx-background-color: rgba(108,99,255,0.05);");

        String[][] data = {
                {"10K+", "Conversations"},
                {"100+", "Knowledge Articles"},
                {"99.9%", "Uptime"},
                {"< 50ms", "Response Time"}
        };

        for (String[] stat : data) {
            VBox item = buildStatItem(stat[0], stat[1]);
            HBox.setHgrow(item, Priority.ALWAYS);
            stats.getChildren().add(item);
        }

        return stats;
    }

    private VBox buildStatItem(String value, String label) {
        VBox item = new VBox(6);
        item.setAlignment(Pos.CENTER);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:40px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");

        // Counter animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), valueLabel);
        scale.setFromX(0.5);
        scale.setToX(1.0);
        scale.setFromY(0.5);
        scale.setToY(1.0);
        scale.play();

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px; -fx-text-fill:#64748B;");

        item.getChildren().addAll(valueLabel, labelLabel);
        return item;
    }

    // ─── CTA SECTION ─────────────────────────────────────────────────────────

    private VBox buildCTASection() {
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(80, 60, 80, 60));
        section.setStyle("-fx-background-color: rgba(22,27,34,0.6);");

        Label title = new Label("Ready to Experience Intelligent AI?");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:36px; -fx-font-weight:bold; -fx-text-fill:white;");
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);

        Label sub = new Label("Join thousands of students and developers using NeuraBot AI every day");
        sub.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-text-fill:#64748B;");

        Button cta = createHeroButton("🚀  Get Started — It's Free", true);
        cta.setPrefWidth(280);
        cta.setOnAction(e -> showLogin());

        section.getChildren().addAll(title, sub, cta);
        return section;
    }

    // ─── FOOTER ──────────────────────────────────────────────────────────────

    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(24, 60, 24, 60));
        footer.setStyle(
                "-fx-background-color: rgba(13,17,23,0.95);" +
                        "-fx-border-color: rgba(108,99,255,0.12);" +
                        "-fx-border-width: 1 0 0 0;"
        );

        Label left = new Label("© 2026 NeuraBot AI. All rights reserved.");
        left.setStyle("-fx-font-size:12px; -fx-text-fill:#374151;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label right = new Label("Developed by Mohammad Sakib Ahmad  •  Version 1.0");
        right.setStyle("-fx-font-size:12px; -fx-text-fill:#374151;");

        footer.getChildren().addAll(left, spacer, right);
        return footer;
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Button createNavButton(String text, boolean primary) {
        Button btn = new Button(text);
        if (primary) {
            btn.setStyle(
                    "-fx-background-color: #6C63FF; -fx-text-fill: white;" +
                            "-fx-font-size: 13px; -fx-font-weight: 600;" +
                            "-fx-background-radius: 8; -fx-padding: 8 20 8 20;" +
                            "-fx-cursor: hand; -fx-font-family:'Segoe UI';"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
                            "-fx-font-size: 13px; -fx-background-radius: 8;" +
                            "-fx-padding: 8 20 8 20; -fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                            "-fx-border-color: rgba(148,163,184,0.3); -fx-border-radius: 8;"
            );
        }
        return btn;
    }

    private Button createHeroButton(String text, boolean primary) {
        Button btn = new Button(text);
        if (primary) {
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                            "-fx-background-radius: 12; -fx-padding: 14 36 14 36;" +
                            "-fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.4), 16, 0, 0, 4);"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #7C73FF, #9B6CF6);" +
                            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                            "-fx-background-radius: 12; -fx-padding: 14 36 14 36;" +
                            "-fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.6), 20, 0, 0, 6);"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #6C63FF, #8B5CF6);" +
                            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 700;" +
                            "-fx-background-radius: 12; -fx-padding: 14 36 14 36;" +
                            "-fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.4), 16, 0, 0, 4);"
            ));
        } else {
            btn.setStyle(
                    "-fx-background-color: rgba(108,99,255,0.1);" +
                            "-fx-text-fill: #A78BFA; -fx-font-size: 15px; -fx-font-weight: 600;" +
                            "-fx-background-radius: 12; -fx-padding: 14 36 14 36;" +
                            "-fx-cursor: hand; -fx-font-family:'Segoe UI';" +
                            "-fx-border-color: rgba(108,99,255,0.3); -fx-border-radius: 12;"
            );
        }
        return btn;
    }

    // ─── PARTICLES ───────────────────────────────────────────────────────────

    private void initParticles() {
        px = new double[PARTICLE_COUNT];
        py = new double[PARTICLE_COUNT];
        pvx = new double[PARTICLE_COUNT];
        pvy = new double[PARTICLE_COUNT];
        pr = new double[PARTICLE_COUNT];
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] = random.nextDouble() * W;
            py[i] = random.nextDouble() * H;
            pvx[i] = (random.nextDouble() - 0.5) * 0.5;
            pvy[i] = (random.nextDouble() - 0.5) * 0.5;
            pr[i] = 1.5 + random.nextDouble() * 2.5;
        }
    }

    private void startParticleAnimation(GraphicsContext gc) {
        particleTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double cW = gc.getCanvas().getWidth();
                double cH = gc.getCanvas().getHeight();
                gc.clearRect(0, 0, cW, cH);
                gc.setFill(Color.web("#0D1117"));
                gc.fillRect(0, 0, cW, cH);

                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    px[i] += pvx[i];
                    py[i] += pvy[i];
                    if (px[i] < 0 || px[i] > cW) pvx[i] = -pvx[i];
                    if (py[i] < 0 || py[i] > cH) pvy[i] = -pvy[i];
                }

                gc.setLineWidth(0.4);
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                        double dx = px[i] - px[j], dy = py[i] - py[j];
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        if (dist < 100) {
                            gc.setStroke(Color.color(0.42, 0.38, 1.0, 0.25 * (1 - dist / 100)));
                            gc.strokeLine(px[i], py[i], px[j], py[j]);
                        }
                    }
                }
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double alpha = 0.5 + 0.4 * Math.sin(now / 1.2e9 + i * 0.3);
                    gc.setFill(Color.color(0.42, 0.38, 1.0, alpha));
                    gc.fillOval(px[i] - pr[i], py[i] - pr[i], pr[i] * 2, pr[i] * 2);
                }
            }
        };
        particleTimer.start();
    }

    private void showLogin() {
        if (particleTimer != null) particleTimer.stop();
        LoginScreen login = new LoginScreen(stage);
        login.show();
    }
}
