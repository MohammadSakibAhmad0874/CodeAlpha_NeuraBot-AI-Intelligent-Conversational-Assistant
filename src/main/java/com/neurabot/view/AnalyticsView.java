package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.analytics.AnalyticsManager;
import com.neurabot.database.DatabaseManager;
import com.neurabot.model.ActivityLog;
import com.neurabot.model.FAQ;
import com.neurabot.model.User;
import com.neurabot.reports.ReportGenerator;
import com.neurabot.util.NotificationManager;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.*;

/**
 * Analytics & Reports view with bar charts, pie charts, top topics.
 */
public class AnalyticsView {

    private final Stage stage;
    private final User user;
    private final AnalyticsManager analytics;
    private final DatabaseManager db;
    private final ReportGenerator reportGen;
    private final NotificationManager notif;

    public AnalyticsView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.analytics = new AnalyticsManager();
        this.db = DatabaseManager.getInstance();
        this.reportGen = new ReportGenerator();
        this.notif = new NotificationManager(stage);
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

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:#161B22; -fx-border-color:rgba(108,99,255,0.15); -fx-border-width:0 1 0 0;");

        Button backBtn = new Button("← Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-background-color:rgba(108,99,255,0.1); -fx-text-fill:#A78BFA; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
        backBtn.setOnAction(e -> goBack());

        Label reportLabel = new Label("Export Reports");
        reportLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#374151; -fx-font-weight:600; -fx-padding:10 0 4 4;");

        Button txtReport = buildSideBtn("📄 User Report (TXT)");
        txtReport.setOnAction(e -> exportUserReport("txt"));

        Button csvReport = buildSideBtn("📊 Sessions (CSV)");
        csvReport.setOnAction(e -> exportSessionsCSV());

        Button perfReport = buildSideBtn("🤖 AI Performance");
        perfReport.setOnAction(e -> exportPerformanceReport());

        Button faqCSV = buildSideBtn("📚 FAQ Export (CSV)");
        faqCSV.setOnAction(e -> exportFAQCSV());

        sidebar.getChildren().addAll(backBtn, new Separator(), reportLabel,
                txtReport, csvReport, perfReport, faqCSV);
        return sidebar;
    }

    private Button buildSideBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color:rgba(108,99,255,0.08); -fx-text-fill:#94A3B8; -fx-background-radius:8; -fx-padding:9 12; -fx-cursor:hand; -fx-font-size:12px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(108,99,255,0.15); -fx-text-fill:white; -fx-background-radius:8; -fx-padding:9 12; -fx-cursor:hand; -fx-font-size:12px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:rgba(108,99,255,0.08); -fx-text-fill:#94A3B8; -fx-background-radius:8; -fx-padding:9 12; -fx-cursor:hand; -fx-font-size:12px;"));
        return btn;
    }

    // ─── MAIN CONTENT ────────────────────────────────────────────────────────

    private ScrollPane buildContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(28, 32, 32, 32));

        // Header
        VBox header = new VBox(4);
        Label title = new Label("📊 Analytics Dashboard");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Real-time AI conversation insights and performance metrics");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");
        header.getChildren().addAll(title, sub);

        // Overview cards row
        HBox overviewRow = buildOverviewCards();

        // Charts row: bar chart + pie chart
        HBox chartsRow = new HBox(24);
        chartsRow.getChildren().addAll(buildActivityBarChart(), buildSentimentPieChart());

        // Top topics + Most asked FAQs
        HBox bottomRow = new HBox(24);
        bottomRow.getChildren().addAll(buildTopTopics(), buildTopFAQs());

        content.getChildren().addAll(header, overviewRow, chartsRow, bottomRow);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    // ─── OVERVIEW CARDS ──────────────────────────────────────────────────────

    private HBox buildOverviewCards() {
        HBox row = new HBox(16);

        Object[][] cards = {
                {"💬", String.valueOf(analytics.getTotalSessions()), "Total Sessions", "#6C63FF"},
                {"📨", String.valueOf(analytics.getTotalMessages()), "Total Messages", "#22C55E"},
                {"👥", String.valueOf(analytics.getTotalUsers()), "Users", "#F59E0B"},
                {"📚", String.valueOf(analytics.getTotalFAQs()), "Knowledge Articles", "#3B82F6"},
                {"😊", String.format("%.0f%%", analytics.getUserSatisfactionRate()), "Satisfaction", "#EC4899"}
        };

        for (Object[] card : cards) {
            VBox c = buildMetricCard((String)card[0], (String)card[1], (String)card[2], (String)card[3]);
            HBox.setHgrow(c, Priority.ALWAYS);
            row.getChildren().add(c);
        }
        return row;
    }

    private VBox buildMetricCard(String icon, String value, String label, String accent) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 18, 18, 18));
        card.setMinHeight(100);
        card.setStyle(
                "-fx-background-color:#161B22; -fx-background-radius:14;" +
                "-fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:14; -fx-border-width:1;"
        );

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size:20px;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:28px; -fx-font-weight:900; -fx-text-fill:" + accent + ";");

        ScaleTransition st = new ScaleTransition(Duration.millis(700), valueLbl);
        st.setFromX(0.3); st.setToX(1); st.setFromY(0.3); st.setToY(1); st.play();

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B; -fx-font-family:'Segoe UI';");

        card.getChildren().addAll(iconLbl, valueLbl, labelLbl);
        return card;
    }

    // ─── BAR CHART (Activity by Day) ─────────────────────────────────────────

    private VBox buildActivityBarChart() {
        VBox card = buildChartCard("📈 Conversation Volume (Last 7 Days)");
        HBox.setHgrow(card, Priority.ALWAYS);

        Map<LocalDate, Integer> activity = analytics.getActivityByDay(7);
        if (activity.isEmpty()) {
            card.getChildren().add(new Label("No data available."));
            return card;
        }

        Canvas canvas = new Canvas(540, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawBarChart(gc, activity, 540, 200);

        card.getChildren().add(canvas);

        // Animate bars
        FadeTransition fade = new FadeTransition(Duration.millis(800), canvas);
        fade.setFromValue(0); fade.setToValue(1); fade.play();

        return card;
    }

    private void drawBarChart(GraphicsContext gc, Map<LocalDate, Integer> data, double width, double height) {
        gc.clearRect(0, 0, width, height);

        List<Map.Entry<LocalDate, Integer>> entries = new ArrayList<>(data.entrySet());
        int n = entries.size();
        if (n == 0) return;

        int maxVal = entries.stream().mapToInt(Map.Entry::getValue).max().orElse(1);
        double barWidth = (width - 60) / n - 8;
        double chartH = height - 50;
        double startX = 40;

        // Grid lines
        gc.setStroke(Color.color(1, 1, 1, 0.05));
        gc.setLineWidth(1);
        for (int i = 0; i <= 4; i++) {
            double y = 10 + (chartH * i / 4);
            gc.strokeLine(startX, y, width - 10, y);
            gc.setFill(Color.color(0.4, 0.45, 0.55, 0.7));
            gc.fillText(String.valueOf(maxVal - maxVal * i / 4), 0, y + 4);
        }

        // Bars
        for (int i = 0; i < n; i++) {
            Map.Entry<LocalDate, Integer> entry = entries.get(i);
            int val = entry.getValue();
            double barH = (double) val / maxVal * chartH;
            double x = startX + i * (barWidth + 8);
            double y = 10 + chartH - barH;

            // Gradient fill
            gc.setFill(Color.color(0.42, 0.39, 1.0, 0.85));
            gc.fillRoundRect(x, y, barWidth, barH, 6, 6);

            // Accent top
            gc.setFill(Color.color(0.67, 0.55, 1.0, 0.9));
            gc.fillRoundRect(x, y, barWidth, Math.min(barH, 6), 6, 6);

            // Day label
            gc.setFill(Color.color(0.4, 0.45, 0.55, 1.0));
            String dayLabel = entry.getKey().getDayOfWeek().toString().substring(0, 3);
            gc.fillText(dayLabel, x + barWidth / 2 - 10, height - 10);

            // Value on top
            if (val > 0) {
                gc.setFill(Color.color(0.67, 0.55, 1.0, 0.9));
                gc.fillText(String.valueOf(val), x + barWidth / 2 - 6, y - 4);
            }
        }
    }

    // ─── PIE CHART (Sentiment) ────────────────────────────────────────────────

    private VBox buildSentimentPieChart() {
        VBox card = buildChartCard("😊 User Sentiment Distribution");
        card.setPrefWidth(320);
        card.setMinWidth(320);

        Map<String, Double> sentiment = analytics.getSentimentDistribution();

        Canvas canvas = new Canvas(280, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawPieChart(gc, sentiment, 280, 200);

        // Legend
        VBox legend = new VBox(6);
        Map<String, Color> colors = new LinkedHashMap<>();
        colors.put("Positive", Color.color(0.13, 0.77, 0.37));
        colors.put("Neutral", Color.color(0.42, 0.39, 1.0));
        colors.put("Negative", Color.color(0.94, 0.27, 0.27));

        for (Map.Entry<String, Double> entry : sentiment.entrySet()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("●");
            Color c = colors.getOrDefault(entry.getKey(), Color.GRAY);
            dot.setStyle("-fx-text-fill: rgb(" + (int)(c.getRed()*255) + "," +
                    (int)(c.getGreen()*255) + "," + (int)(c.getBlue()*255) + "); -fx-font-size:14px;");
            Label lbl = new Label(entry.getKey() + ": " + String.format("%.1f%%", entry.getValue()));
            lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#94A3B8;");
            row.getChildren().addAll(dot, lbl);
            legend.getChildren().add(row);
        }

        card.getChildren().addAll(canvas, legend);

        FadeTransition fade = new FadeTransition(Duration.millis(900), canvas);
        fade.setFromValue(0); fade.setToValue(1); fade.play();

        return card;
    }

    private void drawPieChart(GraphicsContext gc, Map<String, Double> data, double width, double height) {
        gc.clearRect(0, 0, width, height);

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double cx = width / 2, cy = height / 2, r = Math.min(cx, cy) - 10;
        double startAngle = -90;

        Color[] colors = {
                Color.color(0.13, 0.77, 0.37, 0.9),
                Color.color(0.42, 0.39, 1.0, 0.9),
                Color.color(0.94, 0.27, 0.27, 0.9)
        };

        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            gc.setFill(colors[i % colors.length]);
            gc.fillArc(cx - r, cy - r, r * 2, r * 2, startAngle, -angle,
                    javafx.scene.shape.ArcType.ROUND);

            // Outline
            gc.setStroke(Color.color(0.05, 0.07, 0.09, 1.0));
            gc.setLineWidth(2);
            gc.strokeArc(cx - r, cy - r, r * 2, r * 2, startAngle, -angle,
                    javafx.scene.shape.ArcType.ROUND);

            startAngle -= angle;
            i++;
        }

        // Center hole for donut effect
        gc.setFill(Color.color(0.086, 0.11, 0.14, 1.0));
        double hr = r * 0.55;
        gc.fillOval(cx - hr, cy - hr, hr * 2, hr * 2);

        // Center text
        gc.setFill(Color.WHITE);
        gc.fillText("Sentiment", cx - 24, cy - 5);
        gc.setFill(Color.color(0.42, 0.39, 1.0));
        gc.fillText("Analysis", cx - 20, cy + 12);
    }

    // ─── TOP TOPICS ──────────────────────────────────────────────────────────

    private VBox buildTopTopics() {
        VBox card = buildChartCard("🏆 Top Knowledge Categories");
        HBox.setHgrow(card, Priority.ALWAYS);

        List<Map.Entry<String, Integer>> topics = analytics.getTopTopics(8);
        int max = topics.isEmpty() ? 1 : topics.get(0).getValue();

        VBox list = new VBox(10);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topics) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label rankLbl = new Label(String.format("#%d", rank++));
            rankLbl.setStyle("-fx-font-size:12px; -fx-text-fill:#374151; -fx-min-width:28;");

            Label topicLbl = new Label(entry.getKey());
            topicLbl.setStyle("-fx-font-size:13px; -fx-text-fill:#94A3B8; -fx-min-width:160; -fx-font-family:'Segoe UI';");

            // Progress bar
            ProgressBar pb = new ProgressBar((double) entry.getValue() / max);
            pb.setPrefWidth(120);
            pb.setPrefHeight(6);
            pb.setStyle("-fx-accent:#6C63FF; -fx-background-radius:4;");
            HBox.setHgrow(pb, Priority.ALWAYS);

            Label countLbl = new Label(String.valueOf(entry.getValue()));
            countLbl.setStyle("-fx-font-size:12px; -fx-text-fill:#6C63FF; -fx-font-weight:600;");

            row.getChildren().addAll(rankLbl, topicLbl, pb, countLbl);

            // Slide in
            TranslateTransition slide = new TranslateTransition(Duration.millis(400 + rank * 50), row);
            slide.setFromX(-20); slide.setToX(0); slide.play();

            list.getChildren().add(row);
        }

        card.getChildren().add(list);
        return card;
    }

    // ─── TOP FAQS ────────────────────────────────────────────────────────────

    private VBox buildTopFAQs() {
        VBox card = buildChartCard("🔥 Most Asked Questions");
        card.setPrefWidth(380);
        card.setMinWidth(380);

        List<FAQ> topFAQs = analytics.getMostAskedFAQs(7);
        VBox list = new VBox(8);

        if (topFAQs.isEmpty()) {
            Label empty = new Label("Start chatting to see popular questions!");
            empty.setStyle("-fx-text-fill:#374151; -fx-font-size:13px;");
            list.getChildren().add(empty);
        } else {
            for (int i = 0; i < topFAQs.size(); i++) {
                FAQ faq = topFAQs.get(i);
                HBox row = new HBox(10);
                row.setPadding(new Insets(8, 10, 8, 10));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color:rgba(108,99,255,0.05); -fx-background-radius:8;");

                Label num = new Label("#" + (i + 1));
                num.setStyle("-fx-font-size:11px; -fx-text-fill:#6C63FF; -fx-min-width:24; -fx-font-weight:700;");

                Label q = new Label(faq.getQuestion().length() > 42
                        ? faq.getQuestion().substring(0, 42) + "..."
                        : faq.getQuestion());
                q.setStyle("-fx-font-size:12px; -fx-text-fill:#94A3B8;");
                HBox.setHgrow(q, Priority.ALWAYS);

                Label hits = new Label("🔥 " + faq.getHitCount());
                hits.setStyle("-fx-font-size:11px; -fx-text-fill:#F59E0B;");

                row.getChildren().addAll(num, q, hits);
                list.getChildren().add(row);
            }
        }

        card.getChildren().add(list);
        return card;
    }

    private VBox buildChartCard(String title) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setStyle(
                "-fx-background-color:#161B22; -fx-background-radius:14;" +
                "-fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:14; -fx-border-width:1;"
        );

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:white;");
        card.getChildren().add(titleLbl);
        return card;
    }

    // ─── EXPORT ACTIONS ──────────────────────────────────────────────────────

    private void exportUserReport(String format) {
        String report = reportGen.generateUserReport(user);
        String path = reportGen.saveReport(report, "user");
        if (path != null) {
            notif.success("✅ User report saved to:\n" + path);
            db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.REPORT_EXPORTED, "User report TXT");
        } else notif.error("Failed to save report.");
    }

    private void exportSessionsCSV() {
        String csv = reportGen.generateConversationCSV(user);
        String path = reportGen.saveCSV(csv, "sessions");
        if (path != null) {
            notif.success("✅ Sessions exported to:\n" + path);
        } else notif.error("Failed to export sessions.");
    }

    private void exportPerformanceReport() {
        String report = reportGen.generateAIPerformanceReport();
        String path = reportGen.saveReport(report, "performance");
        if (path != null) {
            notif.success("✅ AI Performance report saved!");
            db.logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.REPORT_EXPORTED, "AI Performance report");
        } else notif.error("Failed to save report.");
    }

    private void exportFAQCSV() {
        String csv = reportGen.generateFAQCSV();
        String path = reportGen.saveCSV(csv, "faqs");
        if (path != null) {
            notif.success("✅ FAQ data exported to CSV!");
        } else notif.error("Failed to export FAQs.");
    }

    private void goBack() {
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }
}
