package com.neurabot.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neurabot.model.*;
import com.neurabot.util.FileManager;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central data persistence layer using JSON files.
 * Singleton pattern - one instance manages all data operations.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private final Gson gson;
    private final FileManager fileManager;

    // In-memory stores
    private Map<String, User> users;
    private Map<String, ChatSession> sessions;
    private List<ActivityLog> activityLogs;
    private Map<String, UserSettings> userSettings;

    // Credentials for demo admin
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD_HASH = hashPassword("admin123");
    private static final String DEMO_USERNAME = "demo";
    private static final String DEMO_PASSWORD_HASH = hashPassword("demo123");

    private DatabaseManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.fileManager = new FileManager();
        this.users = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.activityLogs = Collections.synchronizedList(new ArrayList<>());
        this.userSettings = new ConcurrentHashMap<>();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public void initialize() {
        fileManager.ensureDataDirectories();
        loadAllData();
        seedDefaultUsers();
    }

    // ─── USER OPERATIONS ────────────────────────────────────────────────────

    public boolean registerUser(User user) {
        if (usernameExists(user.getUsername())) return false;
        if (emailExists(user.getEmail())) return false;
        users.put(user.getId(), user);
        userSettings.put(user.getId(), new UserSettings(user.getId()));
        saveUsers();
        logActivity(user.getId(), user.getUsername(), ActivityLog.LogType.REGISTER,
                "New user registered: " + user.getUsername());
        return true;
    }

    public User authenticate(String username, String password) {
        String hash = hashPassword(password);
        return users.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                        && u.getPasswordHash().equals(hash)
                        && u.isActive())
                .findFirst()
                .orElse(null);
    }

    public void updateUserLastLogin(String userId) {
        User u = users.get(userId);
        if (u != null) {
            u.setLastLogin(LocalDateTime.now());
            u.setTotalSessions(u.getTotalSessions() + 1);
            saveUsers();
        }
    }

    public void incrementUserMessages(String userId) {
        User u = users.get(userId);
        if (u != null) {
            u.setTotalMessages(u.getTotalMessages() + 1);
            saveUsers();
        }
    }

    public boolean usernameExists(String username) {
        return users.values().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(String id) {
        return users.get(id);
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
        saveUsers();
    }

    public void deleteUser(String userId) {
        users.remove(userId);
        saveUsers();
    }

    // ─── SESSION OPERATIONS ─────────────────────────────────────────────────

    public void saveSession(ChatSession session) {
        sessions.put(session.getId(), session);
        saveSessions();
    }

    public List<ChatSession> getSessionsByUser(String userId) {
        return sessions.values().stream()
                .filter(s -> userId.equals(s.getUserId()))
                .sorted(Comparator.comparing(ChatSession::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    public List<ChatSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    public ChatSession getSessionById(String id) {
        return sessions.get(id);
    }

    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
        saveSessions();
    }

    public int getTotalMessageCount() {
        return sessions.values().stream()
                .mapToInt(ChatSession::getMessageCount)
                .sum();
    }

    // ─── ACTIVITY LOG ───────────────────────────────────────────────────────

    public void logActivity(String userId, String username, ActivityLog.LogType type, String description) {
        ActivityLog log = new ActivityLog(userId, username, type, description);
        activityLogs.add(log);
        // Keep last 1000 entries
        if (activityLogs.size() > 1000) {
            activityLogs = new ArrayList<>(activityLogs.subList(activityLogs.size() - 1000, activityLogs.size()));
        }
        saveLogs();
    }

    public List<ActivityLog> getActivityLogs() {
        List<ActivityLog> copy = new ArrayList<>(activityLogs);
        copy.sort(Comparator.comparing(ActivityLog::getTimestamp).reversed());
        return copy;
    }

    public List<ActivityLog> getActivityLogsByUser(String userId) {
        return activityLogs.stream()
                .filter(l -> userId.equals(l.getUserId()))
                .sorted(Comparator.comparing(ActivityLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    // ─── USER SETTINGS ──────────────────────────────────────────────────────

    public UserSettings getUserSettings(String userId) {
        return userSettings.getOrDefault(userId, new UserSettings(userId));
    }

    public void saveUserSettings(UserSettings settings) {
        userSettings.put(settings.getUserId(), settings);
        saveSettings();
    }

    // ─── PERSISTENCE ────────────────────────────────────────────────────────

    private void loadAllData() {
        loadUsers();
        loadSessions();
        loadLogs();
        loadSettings();
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        String json = fileManager.readFile("users.json");
        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, User>>() {}.getType();
                Map<String, User> loaded = gson.fromJson(json, type);
                if (loaded != null) users.putAll(loaded);
            } catch (Exception e) {
                System.err.println("Error loading users: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSessions() {
        String json = fileManager.readFile("sessions.json");
        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, ChatSession>>() {}.getType();
                Map<String, ChatSession> loaded = gson.fromJson(json, type);
                if (loaded != null) sessions.putAll(loaded);
            } catch (Exception e) {
                System.err.println("Error loading sessions: " + e.getMessage());
            }
        }
    }

    private void loadLogs() {
        String json = fileManager.readFile("activity_logs.json");
        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<List<ActivityLog>>() {}.getType();
                List<ActivityLog> loaded = gson.fromJson(json, type);
                if (loaded != null) activityLogs.addAll(loaded);
            } catch (Exception e) {
                System.err.println("Error loading logs: " + e.getMessage());
            }
        }
    }

    private void loadSettings() {
        String json = fileManager.readFile("settings.json");
        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, UserSettings>>() {}.getType();
                Map<String, UserSettings> loaded = gson.fromJson(json, type);
                if (loaded != null) userSettings.putAll(loaded);
            } catch (Exception e) {
                System.err.println("Error loading settings: " + e.getMessage());
            }
        }
    }

    public void saveUsers() {
        fileManager.writeFile("users.json", gson.toJson(users));
    }

    public void saveSessions() {
        fileManager.writeFile("sessions.json", gson.toJson(sessions));
    }

    public void saveLogs() {
        fileManager.writeFile("activity_logs.json", gson.toJson(activityLogs));
    }

    public void saveSettings() {
        fileManager.writeFile("settings.json", gson.toJson(userSettings));
    }

    public void shutdown() {
        saveUsers();
        saveSessions();
        saveLogs();
        saveSettings();
    }

    // ─── SEED DATA ──────────────────────────────────────────────────────────

    private void seedDefaultUsers() {
        if (!usernameExists(ADMIN_USERNAME)) {
            User admin = new User("Administrator", "admin@neurabot.ai", ADMIN_USERNAME, ADMIN_PASSWORD_HASH);
            admin.setRole("admin");
            admin.setTotalSessions(42);
            admin.setTotalMessages(380);
            users.put(admin.getId(), admin);
            userSettings.put(admin.getId(), new UserSettings(admin.getId()));
        }
        if (!usernameExists(DEMO_USERNAME)) {
            User demo = new User("Demo User", "demo@neurabot.ai", DEMO_USERNAME, DEMO_PASSWORD_HASH);
            demo.setTotalSessions(8);
            demo.setTotalMessages(56);
            users.put(demo.getId(), demo);
            userSettings.put(demo.getId(), new UserSettings(demo.getId()));
        }
        saveUsers();
    }

    // ─── UTILITIES ──────────────────────────────────────────────────────────

    public static String hashPassword(String password) {
        // Simple hash for demo - in production use BCrypt
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    // Stats for dashboard
    public long getTotalUsers() { return users.size(); }
    public long getTotalSessions() { return sessions.size(); }
    public long getTotalMessages() { return users.values().stream().mapToLong(User::getTotalMessages).sum(); }
    public long getActiveUsers() { return users.values().stream().filter(User::isActive).count(); }
}
