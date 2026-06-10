package com.neurabot.nlp;

import com.neurabot.model.Message;

import java.util.*;

/**
 * Analyzes sentiment of user input: Positive, Neutral, or Negative.
 */
public class SentimentAnalyzer {

    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "good", "great", "excellent", "amazing", "awesome", "fantastic", "wonderful",
            "love", "like", "enjoy", "happy", "helpful", "useful", "brilliant", "perfect",
            "nice", "cool", "impressive", "outstanding", "superb", "best", "beautiful",
            "thanks", "thank", "grateful", "appreciate", "interesting", "clear", "easy",
            "yes", "sure", "absolutely", "definitely", "correct", "right", "agree"
    ));

    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
            "bad", "terrible", "awful", "horrible", "hate", "dislike", "poor", "worst",
            "wrong", "incorrect", "useless", "broken", "stupid", "difficult", "hard",
            "confused", "confusing", "unclear", "frustrating", "annoying", "boring",
            "slow", "ugly", "fail", "error", "problem", "issue", "bug", "crash",
            "no", "not", "never", "don't", "cant", "cannot", "wont", "wouldn't",
            "disappointed", "unhappy", "upset", "angry", "sad"
    ));

    private static final Set<String> INTENSIFIERS = new HashSet<>(Arrays.asList(
            "very", "extremely", "absolutely", "completely", "totally", "really", "so", "too", "quite"
    ));

    private final NLPProcessor nlp = new NLPProcessor();

    /**
     * Analyze sentiment of a text string.
     */
    public Message.Sentiment analyze(String text) {
        if (text == null || text.trim().isEmpty()) return Message.Sentiment.NEUTRAL;

        List<String> tokens = nlp.tokenize(text);
        int positiveScore = 0;
        int negativeScore = 0;
        boolean nextIsIntensified = false;

        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i);
            int weight = nextIsIntensified ? 2 : 1;
            nextIsIntensified = false;

            if (INTENSIFIERS.contains(word)) {
                nextIsIntensified = true;
                continue;
            }

            if (POSITIVE_WORDS.contains(word)) {
                positiveScore += weight;
            } else if (NEGATIVE_WORDS.contains(word)) {
                negativeScore += weight;
            }
        }

        // Exclamation mark boosts positivity
        if (text.contains("!")) positiveScore++;

        // Question mark usually neutral
        if (text.contains("?") && positiveScore == 0 && negativeScore == 0) {
            return Message.Sentiment.NEUTRAL;
        }

        if (positiveScore > negativeScore) return Message.Sentiment.POSITIVE;
        if (negativeScore > positiveScore) return Message.Sentiment.NEGATIVE;
        return Message.Sentiment.NEUTRAL;
    }

    /**
     * Get a descriptive label with emoji for the sentiment.
     */
    public String getSentimentLabel(Message.Sentiment sentiment) {
        return switch (sentiment) {
            case POSITIVE -> "😊 Positive";
            case NEGATIVE -> "😟 Negative";
            case NEUTRAL  -> "😐 Neutral";
        };
    }

    /**
     * Get color for sentiment visualization.
     */
    public String getSentimentColor(Message.Sentiment sentiment) {
        return switch (sentiment) {
            case POSITIVE -> "#22C55E";
            case NEGATIVE -> "#EF4444";
            case NEUTRAL  -> "#94A3B8";
        };
    }

    /**
     * Analyze a batch of texts and return sentiment distribution.
     */
    public Map<Message.Sentiment, Long> analyzeBatch(List<String> texts) {
        Map<Message.Sentiment, Long> distribution = new EnumMap<>(Message.Sentiment.class);
        for (Message.Sentiment s : Message.Sentiment.values()) distribution.put(s, 0L);
        for (String text : texts) {
            Message.Sentiment s = analyze(text);
            distribution.merge(s, 1L, Long::sum);
        }
        return distribution;
    }
}
