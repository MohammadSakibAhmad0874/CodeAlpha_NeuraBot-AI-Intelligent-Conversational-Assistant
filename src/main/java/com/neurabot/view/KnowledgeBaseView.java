package com.neurabot.view;

import com.neurabot.App;
import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.FAQ;
import com.neurabot.model.User;
import com.neurabot.util.NotificationManager;
import javafx.animation.FadeTransition;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

/**
 * Browse, search, add, and manage knowledge base FAQs.
 */
public class KnowledgeBaseView {

    private final Stage stage;
    private final User user;
    private final KnowledgeBase kb;
    private final NotificationManager notif;

    private VBox faqListContainer;
    private TextField searchField;
    private String selectedCategory = "All";

    public KnowledgeBaseView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.kb = KnowledgeBase.getInstance();
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

        loadFAQs("All", "");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:#161B22; -fx-border-color:rgba(108,99,255,0.15); -fx-border-width:0 1 0 0;");

        Button backBtn = new Button("← Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-background-color:rgba(108,99,255,0.1); -fx-text-fill:#A78BFA; -fx-font-size:12px; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
        backBtn.setOnAction(e -> goBack());

        Label catTitle = new Label("Categories");
        catTitle.setStyle("-fx-font-size:11px; -fx-text-fill:#374151; -fx-font-weight:600; -fx-padding:10 0 4 4;");

        VBox catButtons = new VBox(3);
        addCategoryButton(catButtons, "All", "📋");
        for (String cat : kb.getCategories()) {
            addCategoryButton(catButtons, cat, "📁");
        }

        sidebar.getChildren().addAll(backBtn, new Separator(), catTitle, catButtons);
        return sidebar;
    }

    private void addCategoryButton(VBox container, String cat, String icon) {
        Button btn = new Button(icon + "  " + cat);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(cat.equals(selectedCategory)
                ? "-fx-background-color:rgba(108,99,255,0.15); -fx-text-fill:white; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand; -fx-font-size:12px;"
                : "-fx-background-color:transparent; -fx-text-fill:#94A3B8; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand; -fx-font-size:12px;");
        btn.setOnAction(e -> {
            selectedCategory = cat;
            loadFAQs(cat, searchField.getText().trim());
        });
        container.getChildren().add(btn);
    }

    private VBox buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28, 32, 28, 32));

        // Header
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleGroup = new VBox(4);
        Label title = new Label("📚 Knowledge Base");
        title.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label(kb.getTotalFAQCount() + " articles across " + kb.getCategories().size() + " categories");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#64748B;");
        titleGroup.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (user.isAdmin()) {
            Button addBtn = new Button("+ Add FAQ");
            addBtn.setStyle("-fx-background-color:linear-gradient(to right,#6C63FF,#8B5CF6); -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:700; -fx-background-radius:10; -fx-padding:10 20; -fx-cursor:hand;");
            addBtn.setOnAction(e -> showAddFAQDialog());
            header.getChildren().addAll(titleGroup, spacer, addBtn);
        } else {
            header.getChildren().addAll(titleGroup, spacer);
        }

        // Search bar
        HBox searchBar = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("🔍  Search knowledge articles...");
        searchField.setPrefHeight(42);
        searchField.setStyle("-fx-background-color:#161B22; -fx-text-fill:white; -fx-prompt-text-fill:#374151; -fx-background-radius:10; -fx-border-color:rgba(108,99,255,0.2); -fx-border-radius:10; -fx-border-width:1; -fx-padding:0 14 0 14; -fx-font-size:13px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, old, val) -> loadFAQs(selectedCategory, val));

        searchBar.getChildren().add(searchField);

        // FAQ list
        faqListContainer = new VBox(12);
        ScrollPane scroll = new ScrollPane(faqListContainer);
        scroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(header, searchBar, scroll);
        return content;
    }

    private void loadFAQs(String category, String query) {
        faqListContainer.getChildren().clear();
        List<FAQ> faqs;

        if (!query.isEmpty()) {
            faqs = kb.searchFAQs(query);
        } else if ("All".equals(category)) {
            faqs = kb.getAllFAQs();
        } else {
            faqs = kb.getFAQsByCategory(category);
        }

        if (faqs.isEmpty()) {
            Label empty = new Label("No articles found. Try a different search term.");
            empty.setStyle("-fx-text-fill:#374151; -fx-font-size:14px; -fx-padding:20;");
            faqListContainer.getChildren().add(empty);
            return;
        }

        for (FAQ faq : faqs) {
            faqListContainer.getChildren().add(buildFAQCard(faq));
        }
    }

    private VBox buildFAQCard(FAQ faq) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color:#161B22; -fx-background-radius:12; -fx-border-color:rgba(108,99,255,0.12); -fx-border-radius:12; -fx-border-width:1;");

        // Question row (clickable to expand)
        HBox questionRow = new HBox(12);
        questionRow.setAlignment(Pos.CENTER_LEFT);
        questionRow.setPadding(new Insets(14, 18, 14, 18));
        questionRow.setStyle("-fx-cursor:hand;");

        Label catBadge = new Label(faq.getCategory());
        catBadge.setStyle("-fx-background-color:rgba(108,99,255,0.15); -fx-text-fill:#A78BFA; -fx-font-size:10px; -fx-background-radius:4; -fx-padding:2 8;");

        Label questionLabel = new Label(faq.getQuestion());
        questionLabel.setStyle("-fx-font-size:14px; -fx-font-weight:600; -fx-text-fill:white; -fx-font-family:'Segoe UI';");
        HBox.setHgrow(questionLabel, Priority.ALWAYS);

        Label hitsLabel = new Label("🔥 " + faq.getHitCount());
        hitsLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#374151;");

        Label arrow = new Label("▼");
        arrow.setStyle("-fx-font-size:10px; -fx-text-fill:#64748B;");

        questionRow.getChildren().addAll(catBadge, questionLabel, hitsLabel, arrow);

        // Answer (collapsed by default)
        VBox answerBox = new VBox(10);
        answerBox.setPadding(new Insets(0, 18, 16, 18));
        answerBox.setVisible(false);
        answerBox.setManaged(false);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(108,99,255,0.1);");

        Label answerLabel = new Label(faq.getAnswer());
        answerLabel.setWrapText(true);
        answerLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#94A3B8; -fx-line-spacing:3;");

        answerBox.getChildren().addAll(sep, answerLabel);

        // Admin controls
        if (user.isAdmin()) {
            HBox adminControls = new HBox(8);
            adminControls.setPadding(new Insets(8, 0, 0, 0));
            Button editBtn = new Button("✏ Edit");
            editBtn.setStyle("-fx-background-color:rgba(59,130,246,0.15); -fx-text-fill:#60A5FA; -fx-font-size:11px; -fx-background-radius:6; -fx-padding:4 12; -fx-cursor:hand;");
            editBtn.setOnAction(e -> showEditFAQDialog(faq));
            Button deleteBtn = new Button("🗑 Delete");
            deleteBtn.setStyle("-fx-background-color:rgba(239,68,68,0.15); -fx-text-fill:#EF4444; -fx-font-size:11px; -fx-background-radius:6; -fx-padding:4 12; -fx-cursor:hand;");
            deleteBtn.setOnAction(e -> deleteFAQ(faq));
            adminControls.getChildren().addAll(editBtn, deleteBtn);
            answerBox.getChildren().add(adminControls);
        }

        card.getChildren().addAll(questionRow, answerBox);

        // Toggle expand/collapse
        questionRow.setOnMouseClicked(e -> {
            boolean expanded = answerBox.isVisible();
            answerBox.setVisible(!expanded);
            answerBox.setManaged(!expanded);
            arrow.setText(expanded ? "▼" : "▲");
        });

        questionRow.setOnMouseEntered(e -> questionRow.setStyle(
                "-fx-background-color:rgba(108,99,255,0.05); -fx-background-radius:12 12 0 0; -fx-cursor:hand;"));
        questionRow.setOnMouseExited(e -> questionRow.setStyle("-fx-cursor:hand;"));

        return card;
    }

    private void showAddFAQDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New FAQ");
        dialog.setHeaderText("Add a new knowledge base article");

        TextField questionField = new TextField();
        questionField.setPromptText("Question");
        TextArea answerField = new TextArea();
        answerField.setPromptText("Answer");
        answerField.setPrefRowCount(4);
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (e.g., AI, Java)");
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (comma-separated)");

        VBox form = new VBox(10, new Label("Question:"), questionField, new Label("Answer:"), answerField,
                new Label("Category:"), categoryField, new Label("Keywords:"), keywordsField);
        form.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String q = questionField.getText().trim();
                String a = answerField.getText().trim();
                String cat = categoryField.getText().trim();
                String[] kws = keywordsField.getText().split(",");

                if (q.isEmpty() || a.isEmpty() || cat.isEmpty()) {
                    notif.error("All fields are required.");
                    return;
                }

                FAQ faq = new FAQ(q, a, cat, kws);
                kb.addFAQ(faq);
                notif.success("✅ FAQ added successfully!");
                loadFAQs(selectedCategory, "");
            }
        });
    }

    private void showEditFAQDialog(FAQ faq) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit FAQ");

        TextField questionField = new TextField(faq.getQuestion());
        TextArea answerField = new TextArea(faq.getAnswer());
        answerField.setPrefRowCount(4);
        TextField categoryField = new TextField(faq.getCategory());

        VBox form = new VBox(10, new Label("Question:"), questionField,
                new Label("Answer:"), answerField, new Label("Category:"), categoryField);
        form.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                faq.setQuestion(questionField.getText().trim());
                faq.setAnswer(answerField.getText().trim());
                faq.setCategory(categoryField.getText().trim());
                kb.updateFAQ(faq);
                notif.success("✅ FAQ updated!");
                loadFAQs(selectedCategory, "");
            }
        });
    }

    private void deleteFAQ(FAQ faq) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete FAQ");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete: \"" + faq.getQuestion() + "\"?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                kb.deleteFAQ(faq.getId());
                notif.warning("FAQ deleted.");
                loadFAQs(selectedCategory, "");
            }
        });
    }

    private void goBack() {
        MainDashboard dashboard = new MainDashboard(stage, user);
        dashboard.show();
    }
}
