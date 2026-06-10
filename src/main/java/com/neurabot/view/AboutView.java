package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.User;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * About page with project details, developer info, tech stack, and future scope.
 */
public class AboutView {

    private final Stage stage;
    private final User user;

    public AboutView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
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

        sidebar.getChildren().add(backBtn);
        return sidebar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(32);
        content.setPadding(new Insets(36, 50, 40, 50));
        content.setAlignment(Pos.TOP_CENTER);

        // Hero section
        VBox hero = new VBox(12);
        hero.setAlignment(Pos.CENTER);

        Label icon = new Label("🤖");
        icon.setStyle("-fx-font-size:64px;");

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), icon);
        pulse.setFromX(0.9); pulse.setToX(1.05);
        pulse.setAutoReverse(true); pulse.setCycleCount(-1); pulse.play();

        Label appName = new Label("NeuraBot AI");
        appName.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:40px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");

        Label tagline = new Label("Think.  Learn.  Assist.");
        tagline.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-text-fill:#64748B; -fx-letter-spacing:3;");

        Label version = buildBadge("Version 1.0  •  June 2026");
        Label status  = buildBadge("🟢 Active  •  AI-Powered  •  Open Architecture");

        hero.getChildren().addAll(icon, appName, tagline, version, status);

        // About description
        VBox descCard = buildCard("📌 About This Project",
                "NeuraBot AI is an intelligent conversational assistant built as a Final Year B.Tech " +
                        "Computer Science project. It demonstrates the practical application of Natural Language " +
                        "Processing, Intent Detection, Sentiment Analysis, and Knowledge Base Management using " +
                        "modern Java technologies.\n\n" +
                        "The application simulates a professional AI assistant experience similar to ChatGPT, " +
                        "Google Gemini, and Microsoft Copilot — showcasing advanced OOP design, MVC architecture, " +
                        "and premium JavaFX user interfaces.");

        // Developer info
        VBox devCard = buildCard("👨‍💻 Developer Information",
                "Name    :  Mohammad Sakib Ahmad\n" +
                        "Role    :  Lead Developer & AI Engineer\n" +
                        "Branch  :  B.Tech Computer Science & Engineering\n" +
                        "Year    :  Final Year, 2025–2026\n" +
                        "Email   :  sakib@neurabot.ai\n" +
                        "Project :  Final Year B.Tech Project — NeuraBot AI");

        // Tech stack
        VBox techCard = buildTechCard();

        // Stats card
        VBox statsCard = buildStatsCard();

        // Future scope
        VBox futureCard = buildCard("🚀 Future Scope",
                "The following features are planned for NeuraBot AI v2.0:\n\n" +
                        "• 🌐 REST API backend with Spring Boot for web deployment\n" +
                        "• 📱 Android mobile application\n" +
                        "• 🧠 Integration with OpenAI GPT / Google Gemini API\n" +
                        "• 🎤 Full voice recognition with real-time STT/TTS\n" +
                        "• 📊 Advanced ML model training pipeline\n" +
                        "• 🌍 Multi-language support (Hindi, Spanish, French)\n" +
                        "• 📧 Email notification system\n" +
                        "• ☁ Cloud deployment on AWS/GCP\n" +
                        "• 🔒 Two-factor authentication\n" +
                        "• 📈 Advanced analytics with ML-based predictions");

        // Viva questions preview
        VBox vivaCard = buildCard("🎓 Key Viva Topics",
                "Be prepared to answer:\n\n" +
                        "Q1: What is the architecture of NeuraBot AI?\n" +
                        "A: MVC (Model-View-Controller) with OOP principles in Java 17 + JavaFX\n\n" +
                        "Q2: How does the NLP module work?\n" +
                        "A: Tokenization → Stop-word removal → Keyword extraction → Intent detection via pattern matching\n\n" +
                        "Q3: How is data persisted?\n" +
                        "A: JSON files stored in ~/.neurabot/data/ using Google Gson library\n\n" +
                        "Q4: What is the knowledge base matching algorithm?\n" +
                        "A: Weighted keyword overlap scoring with a 0.25 confidence threshold\n\n" +
                        "Q5: How is sentiment analyzed?\n" +
                        "A: Lexicon-based approach using positive/negative word sets with intensifier weighting\n\n" +
                        "→ Full Viva Q&A available in /docs/VIVAQuestions.md");

        // Footer
        Label footer = new Label("© 2026 NeuraBot AI  •  Developed by Mohammad Sakib Ahmad  •  Version 1.0");
        footer.setStyle("-fx-font-size:12px; -fx-text-fill:#374151; -fx-padding:10 0 0 0;");
        footer.setTextAlignment(TextAlignment.CENTER);

        content.getChildren().addAll(hero, descCard, devCard, techCard, statsCard, futureCard, vivaCard, footer);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    private VBox buildCard(String title, String body) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color:#161B22; -fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:14; -fx-border-width:1;");
        card.setMaxWidth(900);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(108,99,255,0.15);");

        Label bodyLbl = new Label(body);
        bodyLbl.setWrapText(true);
        bodyLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#94A3B8; -fx-line-spacing:4;");

        card.getChildren().addAll(titleLbl, sep, bodyLbl);
        return card;
    }

    private VBox buildTechCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color:#161B22; -fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:14; -fx-border-width:1;");
        card.setMaxWidth(900);

        Label title = new Label("🛠 Technology Stack");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(108,99,255,0.15);");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        String[][] techs = {
                {"☕ Java 17+", "Core programming language with modern features"},
                {"🎨 JavaFX 21", "Rich desktop GUI framework with CSS & animations"},
                {"📦 Google Gson", "JSON serialization for data persistence"},
                {"🗄 SQLite JDBC", "Embedded database for structured storage"},
                {"🧠 Rule-Based NLP", "Custom tokenization, intent detection, sentiment analysis"},
                {"🏗 MVC Architecture", "Model-View-Controller design pattern"},
                {"⚙ Maven", "Build automation and dependency management"},
                {"🔐 SHA-256 Hashing", "Secure password storage"}
        };

        for (int i = 0; i < techs.length; i++) {
            Label tech = new Label(techs[i][0]);
            tech.setStyle("-fx-font-size:13px; -fx-font-weight:700; -fx-text-fill:#A78BFA; -fx-min-width:180;");
            Label desc = new Label(techs[i][1]);
            desc.setStyle("-fx-font-size:12px; -fx-text-fill:#64748B;");
            grid.add(tech, (i % 2) * 2, i / 2);
            grid.add(desc, (i % 2) * 2 + 1, i / 2);
        }

        card.getChildren().addAll(title, sep, grid);
        return card;
    }

    private VBox buildStatsCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color:rgba(108,99,255,0.05); -fx-background-radius:14; -fx-border-color:rgba(108,99,255,0.25); -fx-border-radius:14; -fx-border-width:1;");
        card.setMaxWidth(900);
        card.setAlignment(Pos.CENTER);

        Label title = new Label("📊 Project Statistics");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");

        HBox statsRow = new HBox(0);
        statsRow.setAlignment(Pos.CENTER);

        String[][] stats = {
                {"🧠", String.valueOf(KnowledgeBase.getInstance().getTotalFAQCount()) + "+", "Knowledge Articles"},
                {"📦", "20+", "Java Classes"},
                {"🎨", "10+", "UI Screens"},
                {"🤖", "16", "AI Intents"},
                {"💾", "JSON", "Data Storage"}
        };

        for (String[] stat : stats) {
            VBox item = new VBox(4);
            item.setAlignment(Pos.CENTER);
            item.setPadding(new Insets(0, 30, 0, 30));
            Label iconLbl = new Label(stat[0]);
            iconLbl.setStyle("-fx-font-size:24px;");
            Label valueLbl = new Label(stat[1]);
            valueLbl.setStyle("-fx-font-size:22px; -fx-font-weight:900; -fx-text-fill:#6C63FF;");
            Label labelLbl = new Label(stat[2]);
            labelLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B;");
            item.getChildren().addAll(iconLbl, valueLbl, labelLbl);
            statsRow.getChildren().add(item);
        }

        card.getChildren().addAll(title, statsRow);
        return card;
    }

    private Label buildBadge(String text) {
        Label badge = new Label(text);
        badge.setStyle(
                "-fx-background-color:rgba(108,99,255,0.1);" +
                "-fx-border-color:rgba(108,99,255,0.25); -fx-border-radius:20;" +
                "-fx-background-radius:20; -fx-text-fill:#A78BFA;" +
                "-fx-font-size:12px; -fx-padding:5 16 5 16;"
        );
        return badge;
    }

    private void goBack() {
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }
}
