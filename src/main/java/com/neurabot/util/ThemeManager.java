package com.neurabot.util;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages application themes (dark/light) and notifies all registered scenes.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private Theme currentTheme = Theme.DARK;
    private final List<Scene> registeredScenes = new ArrayList<>();
    private final List<Consumer<Theme>> themeChangeListeners = new ArrayList<>();

    private static final String DARK_CSS  = "/styles/dark-theme.css";
    private static final String LIGHT_CSS = "/styles/light-theme.css";

    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            applyTheme(scene);
        }
    }

    public void unregisterScene(Scene scene) {
        registeredScenes.remove(scene);
    }

    public void addThemeChangeListener(Consumer<Theme> listener) {
        themeChangeListeners.add(listener);
    }

    public void toggleTheme() {
        setTheme(currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        for (Scene scene : registeredScenes) {
            applyTheme(scene);
        }
        for (Consumer<Theme> listener : themeChangeListeners) {
            listener.accept(theme);
        }
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String cssPath = currentTheme == Theme.DARK ? DARK_CSS : LIGHT_CSS;
        try {
            String url = getClass().getResource(cssPath).toExternalForm();
            scene.getStylesheets().add(url);
        } catch (Exception e) {
            // CSS file may not exist yet; apply inline defaults
            System.err.println("CSS not found: " + cssPath);
        }
    }

    public Theme getCurrentTheme() { return currentTheme; }
    public boolean isDark() { return currentTheme == Theme.DARK; }

    public String getThemeName() {
        return currentTheme == Theme.DARK ? "Dark Mode" : "Light Mode";
    }

    public String getThemeIcon() {
        return currentTheme == Theme.DARK ? "🌙" : "☀";
    }

    public String getPrimaryColor() {
        return currentTheme == Theme.DARK ? "#6C63FF" : "#4F46E5";
    }

    public String getBackgroundColor() {
        return currentTheme == Theme.DARK ? "#0D1117" : "#F8FAFC";
    }

    public String getSurfaceColor() {
        return currentTheme == Theme.DARK ? "#161B22" : "#FFFFFF";
    }

    public String getTextColor() {
        return currentTheme == Theme.DARK ? "#E6EDF3" : "#0F172A";
    }
}
