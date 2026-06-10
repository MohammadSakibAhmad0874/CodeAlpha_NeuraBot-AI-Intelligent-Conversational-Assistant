package com.neurabot.ai;

import com.neurabot.nlp.NLPProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects the user's intent from natural language input.
 * Uses pattern matching and keyword analysis.
 */
public class IntentDetector {

    public enum Intent {
        GREETING,
        FAREWELL,
        GRATITUDE,
        QUESTION,
        HELP_REQUEST,
        EDUCATIONAL_QUERY,
        SMALL_TALK,
        COMPLAINT,
        COMPLIMENT,
        ABOUT_BOT,
        CAPABILITIES_QUERY,
        JOKE_REQUEST,
        DEFINITION_REQUEST,
        HOW_TO_REQUEST,
        RECOMMENDATION_REQUEST,
        COMPARISON_REQUEST,
        UNKNOWN
    }

    private final NLPProcessor nlp = new NLPProcessor();

    // Pattern → Intent mappings (ordered by priority)
    private final Map<Intent, String[]> patterns = new HashMap<>() {{
        put(Intent.GREETING, new String[]{
                "hello", "hi", "hey", "howdy", "greetings", "good morning", "good afternoon",
                "good evening", "good day", "whats up", "what's up", "sup", "yo", "namaste"
        });
        put(Intent.FAREWELL, new String[]{
                "bye", "goodbye", "see you", "see ya", "later", "farewell", "cya",
                "quit", "exit", "good night", "take care", "catch you later"
        });
        put(Intent.GRATITUDE, new String[]{
                "thank", "thanks", "thank you", "appreciate", "grateful", "much appreciated",
                "awesome thanks", "great thanks", "helpful", "you're great"
        });
        put(Intent.ABOUT_BOT, new String[]{
                "who are you", "what are you", "your name", "about you", "tell me about yourself",
                "are you ai", "are you robot", "are you human", "what kind of ai"
        });
        put(Intent.CAPABILITIES_QUERY, new String[]{
                "what can you do", "your capabilities", "features", "help me", "what do you know",
                "how can you help", "your abilities", "what are you capable"
        });
        put(Intent.JOKE_REQUEST, new String[]{
                "tell me a joke", "joke", "funny", "make me laugh", "humor"
        });
        put(Intent.DEFINITION_REQUEST, new String[]{
                "what is", "what are", "define", "definition of", "meaning of", "explain what",
                "tell me what", "describe what"
        });
        put(Intent.HOW_TO_REQUEST, new String[]{
                "how to", "how do i", "how can i", "how does", "steps to", "guide me",
                "tutorial", "show me how", "how do you"
        });
        put(Intent.RECOMMENDATION_REQUEST, new String[]{
                "recommend", "suggest", "suggestion", "advice", "what should i", "best way",
                "better option", "which is better", "what do you recommend"
        });
        put(Intent.COMPARISON_REQUEST, new String[]{
                "difference between", "compare", "vs", "versus", "which is better",
                "what is the difference", "how do they differ"
        });
        put(Intent.EDUCATIONAL_QUERY, new String[]{
                "java", "python", "algorithm", "machine learning", "artificial intelligence",
                "deep learning", "neural", "database", "sql", "api", "programming",
                "data structure", "operating system", "network", "cloud", "software"
        });
        put(Intent.COMPLAINT, new String[]{
                "not working", "wrong answer", "incorrect", "bad response", "you're wrong",
                "that's wrong", "doesn't work", "error", "issue", "problem", "failed"
        });
        put(Intent.COMPLIMENT, new String[]{
                "great answer", "good job", "well done", "excellent", "amazing", "brilliant",
                "you're smart", "impressive", "perfect answer", "love your answer"
        });
        put(Intent.SMALL_TALK, new String[]{
                "how are you", "how's it going", "hows life", "whats new", "bored",
                "talk to me", "chat", "just checking", "wassup"
        });
        put(Intent.HELP_REQUEST, new String[]{
                "help", "assist", "support", "need help", "i need", "can you help",
                "please help", "guidance", "stuck"
        });
    }};

    /**
     * Detect the most likely intent of the given text.
     */
    public Intent detect(String text) {
        if (text == null || text.trim().isEmpty()) return Intent.UNKNOWN;
        String lower = text.toLowerCase().trim();

        // Check each intent's patterns
        Map<Intent, Integer> scores = new HashMap<>();
        for (Map.Entry<Intent, String[]> entry : patterns.entrySet()) {
            int score = 0;
            for (String pattern : entry.getValue()) {
                if (lower.contains(pattern)) {
                    score += pattern.split("\\s+").length; // longer matches score higher
                }
            }
            if (score > 0) scores.put(entry.getKey(), score);
        }

        if (scores.isEmpty()) {
            // Fallback: if it ends with "?" it's a question
            return nlp.isQuestion(text) ? Intent.QUESTION : Intent.UNKNOWN;
        }

        // Return the intent with the highest score
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Intent.UNKNOWN);
    }

    /**
     * Get a human-readable label for an intent.
     */
    public String getIntentLabel(Intent intent) {
        return switch (intent) {
            case GREETING -> "Greeting";
            case FAREWELL -> "Farewell";
            case GRATITUDE -> "Gratitude";
            case QUESTION -> "Question";
            case HELP_REQUEST -> "Help Request";
            case EDUCATIONAL_QUERY -> "Educational Query";
            case SMALL_TALK -> "Small Talk";
            case COMPLAINT -> "Complaint";
            case COMPLIMENT -> "Compliment";
            case ABOUT_BOT -> "About Bot";
            case CAPABILITIES_QUERY -> "Capabilities";
            case JOKE_REQUEST -> "Joke Request";
            case DEFINITION_REQUEST -> "Definition";
            case HOW_TO_REQUEST -> "How-To";
            case RECOMMENDATION_REQUEST -> "Recommendation";
            case COMPARISON_REQUEST -> "Comparison";
            case UNKNOWN -> "Unknown";
        };
    }
}
