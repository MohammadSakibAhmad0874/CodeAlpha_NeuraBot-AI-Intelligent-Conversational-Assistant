package com.neurabot.view;

import com.neurabot.App;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated splash screen shown on application startup.
 * Features a neural network particle animation and loading progress.
 */
public class SplashScreen {

    private final Stage stage;
    private final Random random = new Random();
    private AnimationTimer particleTimer;

    // Particle system
    private static final int PARTICLE_COUNT = 60;
    private final double[] px = new double[PARTICLE_COUNT];
    private final double[] py = new double[PARTICLE_COUNT];
    private final double[] pvx = new double[PARTICLE_COUNT];
    private final double[] pvy = new double[PARTICLE_COUNT];
    private final double[] pr = new double[PARTICLE_COUNT];
    private static final double W = 700, H = 420;

    public SplashScreen(Stage stage) {
        this.stage = stage;
        initParticles();
    }

    private void initParticles() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] = random.nextDouble() * W;
            py[i] = random.nextDouble() * H;
            pvx[i] = (random.nextDouble() - 0.5) * 0.8;
            pvy[i] = (random.nextDouble() - 0.5) * 0.8;
            pr[i] = 2 + random.nextDouble() * 3;
        }
    }

    public void show() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0D1117;");

        // Particle canvas
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Content overlay
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Neural icon
        Label icon = new Label("🤖");
        icon.setStyle("-fx-font-size: 56px;");

        // Title
        Label title = new Label("NeuraBot AI");
        title.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 42px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: linear-gradient(to right, #6C63FF, #A78BFA);"
        );
        title.setTextFill(Color.web("#6C63FF"));

        // Tagline
        Label tagline = new Label("Think.  Learn.  Assist.");
        tagline.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 15px;" +
                        "-fx-text-fill: #94A3B8;" +
                        "-fx-letter-spacing: 3;"
        );

        // Progress bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(280);
        progressBar.setPrefHeight(4);
        progressBar.setStyle(
                "-fx-accent: #6C63FF;" +
                        "-fx-background-color: #1E2435;" +
                        "-fx-background-radius: 4;" +
                        "-fx-pref-height: 4;"
        );

        Label loadingLabel = new Label("Initializing AI Engine...");
        loadingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        Label version = new Label("Version 1.0  •  Mohammad Sakib Ahmad");
        version.setStyle("-fx-font-size: 10px; -fx-text-fill: #374151;");

        content.getChildren().addAll(icon, title, tagline, progressBar, loadingLabel, version);
        root.getChildren().add(content);

        // Scene setup
        Scene scene = new Scene(root, W, H);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();

        // Fade in
        FadeTransition fade = new FadeTransition(Duration.millis(600), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Particle animation
        startParticleAnimation(gc);

        // Loading progress
        String[] loadingMessages = {
                "Initializing AI Engine...",
                "Loading Knowledge Base...",
                "Training NLP Processor...",
                "Building Intent Detector...",
                "Connecting Analytics...",
                "Preparing Interface...",
                "NeuraBot Ready! 🚀"
        };

        Timeline loadingTimeline = new Timeline();
        for (int i = 0; i < loadingMessages.length; i++) {
            final int index = i;
            loadingTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(300 + index * 250), e -> {
                progressBar.setProgress((double)(index + 1) / loadingMessages.length);
                loadingLabel.setText(loadingMessages[index]);
            }));
        }

        loadingTimeline.setOnFinished(e -> {
            particleTimer.stop();
            showLandingPage();
        });

        loadingTimeline.play();
    }

    private void startParticleAnimation(GraphicsContext gc) {
        particleTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, W, H);

                // Background gradient
                gc.setFill(Color.web("#0D1117"));
                gc.fillRect(0, 0, W, H);

                // Update particles
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    px[i] += pvx[i];
                    py[i] += pvy[i];
                    if (px[i] < 0 || px[i] > W) pvx[i] = -pvx[i];
                    if (py[i] < 0 || py[i] > H) pvy[i] = -pvy[i];
                }

                // Draw connections
                gc.setLineWidth(0.5);
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                        double dx = px[i] - px[j];
                        double dy = py[i] - py[j];
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        if (dist < 90) {
                            double alpha = 0.3 * (1 - dist / 90);
                            gc.setStroke(Color.color(0.42, 0.38, 1.0, alpha));
                            gc.strokeLine(px[i], py[i], px[j], py[j]);
                        }
                    }
                }

                // Draw particles
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double glow = 0.6 + 0.4 * Math.sin(now / 1e9 + i);
                    gc.setFill(Color.color(0.42, 0.38, 1.0, glow));
                    gc.fillOval(px[i] - pr[i], py[i] - pr[i], pr[i] * 2, pr[i] * 2);
                }
            }
        };
        particleTimer.start();
    }

    private void showLandingPage() {
        LandingPage landing = new LandingPage(stage);
        landing.show();
    }
}
