package com.neurabot.reports;

import com.neurabot.analytics.AnalyticsManager;
import com.neurabot.database.DatabaseManager;
import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.ChatSession;
import com.neurabot.model.FAQ;
import com.neurabot.model.Message;
import com.neurabot.model.User;
import com.neurabot.util.FileManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generates text, CSV, and summary reports for users and admins.
 */
public class ReportGenerator {

    private final DatabaseManager db;
    private final KnowledgeBase kb;
    private final AnalyticsManager analytics;
    private final FileManager fileManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public ReportGenerator() {
        this.db = DatabaseManager.getInstance();
        this.kb = KnowledgeBase.getInstance();
        this.analytics = new AnalyticsManager();
        this.fileManager = new FileManager();
    }

    // ─── USER REPORT ─────────────────────────────────────────────────────────

    public String generateUserReport(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("         NEURABOT AI - USER ACTIVITY REPORT\n");
        sb.append("=".repeat(60)).append("\n\n");
        sb.append("Report Generated : ").append(LocalDateTime.now().format(FORMATTER)).append("\n");
        sb.append("User             : ").append(user.getFullName()).append("\n");
        sb.append("Username         : @").append(user.getUsername()).append("\n");
        sb.append("Email            : ").append(user.getEmail()).append("\n");
        sb.append("Account Created  : ").append(user.getFormattedCreatedAt()).append("\n");
        sb.append("Last Login       : ").append(user.getFormattedLastLogin()).append("\n");
        sb.append("Role             : ").append(user.getRole().toUpperCase()).append("\n\n");

        sb.append("-".repeat(60)).append("\n");
        sb.append("CONVERSATION STATISTICS\n");
        sb.append("-".repeat(60)).append("\n");

        List<ChatSession> sessions = db.getSessionsByUser(user.getId());
        int totalMessages = sessions.stream().mapToInt(ChatSession::getMessageCount).sum();

        sb.append("Total Sessions   : ").append(sessions.size()).append("\n");
        sb.append("Total Messages   : ").append(totalMessages).append("\n");
        sb.append("Avg Msgs/Session : ").append(String.format("%.1f",
                sessions.isEmpty() ? 0 : (double) totalMessages / sessions.size())).append("\n\n");

        if (!sessions.isEmpty()) {
            sb.append("-".repeat(60)).append("\n");
            sb.append("RECENT CONVERSATIONS\n");
            sb.append("-".repeat(60)).append("\n");
            int count = 0;
            for (ChatSession session : sessions) {
                if (count++ >= 10) break;
                sb.append("  [").append(session.getFormattedStartTime()).append("] ");
                sb.append(session.getTitle()).append("\n");
                sb.append("  Messages: ").append(session.getMessageCount()).append("\n\n");
            }
        }

        sb.append("=".repeat(60)).append("\n");
        sb.append("© 2026 NeuraBot AI | Developed by Mohammad Sakib Ahmad\n");
        sb.append("=".repeat(60)).append("\n");
        return sb.toString();
    }

    // ─── AI PERFORMANCE REPORT ───────────────────────────────────────────────

    public String generateAIPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("       NEURABOT AI - PERFORMANCE ANALYTICS REPORT\n");
        sb.append("=".repeat(60)).append("\n\n");
        sb.append("Report Generated : ").append(LocalDateTime.now().format(FORMATTER)).append("\n\n");

        sb.append("-".repeat(60)).append("\n");
        sb.append("SYSTEM OVERVIEW\n");
        sb.append("-".repeat(60)).append("\n");
        sb.append("Total Users      : ").append(analytics.getTotalUsers()).append("\n");
        sb.append("Total Sessions   : ").append(analytics.getTotalSessions()).append("\n");
        sb.append("Total Messages   : ").append(analytics.getTotalMessages()).append("\n");
        sb.append("Knowledge Base   : ").append(analytics.getTotalFAQs()).append(" articles\n");
        sb.append("Avg Msgs/Session : ").append(String.format("%.1f", analytics.getAverageMessagesPerSession())).append("\n");
        sb.append("User Satisfaction: ").append(String.format("%.1f%%", analytics.getUserSatisfactionRate())).append("\n\n");

        sb.append("-".repeat(60)).append("\n");
        sb.append("TOP KNOWLEDGE CATEGORIES\n");
        sb.append("-".repeat(60)).append("\n");
        List<Map.Entry<String, Integer>> topTopics = analytics.getTopTopics(10);
        for (int i = 0; i < topTopics.size(); i++) {
            Map.Entry<String, Integer> entry = topTopics.get(i);
            sb.append(String.format("  %2d. %-25s %d queries\n", i + 1, entry.getKey(), entry.getValue()));
        }

        sb.append("\n");
        sb.append("-".repeat(60)).append("\n");
        sb.append("MOST ASKED QUESTIONS (TOP 10)\n");
        sb.append("-".repeat(60)).append("\n");
        List<FAQ> topFAQs = analytics.getMostAskedFAQs(10);
        if (topFAQs.isEmpty()) {
            sb.append("  No FAQ data available yet.\n");
        } else {
            for (int i = 0; i < topFAQs.size(); i++) {
                FAQ faq = topFAQs.get(i);
                sb.append(String.format("  %2d. [%s] %s (%d hits)\n",
                        i + 1, faq.getCategory(), faq.getQuestion(), faq.getHitCount()));
            }
        }

        sb.append("\n");
        sb.append("-".repeat(60)).append("\n");
        sb.append("SENTIMENT ANALYSIS\n");
        sb.append("-".repeat(60)).append("\n");
        Map<String, Double> sentiment = analytics.getSentimentDistribution();
        sentiment.forEach((key, val) ->
                sb.append(String.format("  %-12s: %.1f%%\n", key, val)));

        sb.append("\n=".repeat(60).substring(1)).append("\n");
        sb.append("© 2026 NeuraBot AI | Developed by Mohammad Sakib Ahmad\n");
        sb.append("=".repeat(60)).append("\n");
        return sb.toString();
    }

    // ─── CSV EXPORTS ─────────────────────────────────────────────────────────

    public String generateConversationCSV(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Session ID,Session Title,Start Time,Message Count,Messages\n");
        for (ChatSession session : db.getSessionsByUser(user.getId())) {
            sb.append(String.format("\"%s\",\"%s\",\"%s\",%d\n",
                    session.getId(), session.getTitle(),
                    session.getFormattedStartTime(), session.getMessageCount()));
        }
        return sb.toString();
    }

    public String generateFAQCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Category,Question,Hit Count,Active\n");
        for (FAQ faq : kb.getAllFAQs()) {
            String question = faq.getQuestion().replace("\"", "'");
            sb.append(String.format("\"%s\",\"%s\",\"%s\",%d,%s\n",
                    faq.getId(), faq.getCategory(), question,
                    faq.getHitCount(), faq.isActive()));
        }
        return sb.toString();
    }

    // ─── SAVE TO FILE ────────────────────────────────────────────────────────

    public String saveReport(String content, String type) {
        String timestamp = LocalDateTime.now().format(FILE_FORMATTER);
        String filename = "neurabot_" + type + "_" + timestamp + ".txt";
        boolean saved = fileManager.writeReport(filename, content);
        return saved ? fileManager.getReportsDir() + "\\" + filename : null;
    }

    public String saveCSV(String content, String type) {
        String timestamp = LocalDateTime.now().format(FILE_FORMATTER);
        String filename = "neurabot_" + type + "_" + timestamp + ".csv";
        boolean saved = fileManager.writeReport(filename, content);
        return saved ? fileManager.getReportsDir() + "\\" + filename : null;
    }
}
