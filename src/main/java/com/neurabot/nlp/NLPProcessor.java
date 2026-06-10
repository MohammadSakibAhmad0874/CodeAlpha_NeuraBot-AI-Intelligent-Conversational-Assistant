package com.neurabot.nlp;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Core Natural Language Processing engine.
 * Provides tokenization, keyword extraction, and text analysis.
 */
public class NLPProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could", "should",
            "may", "might", "shall", "can", "need", "dare", "ought", "used",
            "i", "me", "my", "we", "our", "you", "your", "he", "she", "it", "they", "them",
            "this", "that", "these", "those", "what", "which", "who", "whom", "whose",
            "when", "where", "why", "how", "and", "but", "or", "nor", "for", "so", "yet",
            "in", "on", "at", "by", "with", "about", "above", "after", "before", "between",
            "into", "through", "during", "to", "from", "up", "down", "out", "off", "over",
            "of", "not", "no", "yes", "ok", "okay", "please", "tell", "me", "give",
            "explain", "describe", "define", "show", "help", "want", "know"
    ));

    /**
     * Tokenize text into individual words (lowercase, letters only).
     */
    public List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        String clean = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return Arrays.stream(clean.split("\\s+"))
                .filter(w -> w.length() > 1)
                .collect(Collectors.toList());
    }

    /**
     * Extract meaningful keywords by removing stop words.
     */
    public List<String> extractKeywords(String text) {
        return tokenize(text).stream()
                .filter(w -> !STOP_WORDS.contains(w) && w.length() >= 2)
                .collect(Collectors.toList());
    }

    /**
     * Compute keyword frequency map.
     */
    public Map<String, Integer> getWordFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : extractKeywords(text)) {
            freq.merge(word, 1, Integer::sum);
        }
        return freq;
    }

    /**
     * Normalize text: lowercase, collapse whitespace, remove punctuation.
     */
    public String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Split text into sentences.
     */
    public List<String> sentenceSplit(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        String[] sentences = text.split("[.!?]+\\s*");
        return Arrays.stream(sentences)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Check if text contains any of the given patterns (case-insensitive).
     */
    public boolean containsAny(String text, String... patterns) {
        String lower = text.toLowerCase();
        for (String p : patterns) {
            if (lower.contains(p.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * Compute Jaccard similarity between two texts (keyword sets).
     */
    public double jaccardSimilarity(String text1, String text2) {
        Set<String> kw1 = new HashSet<>(extractKeywords(text1));
        Set<String> kw2 = new HashSet<>(extractKeywords(text2));
        if (kw1.isEmpty() && kw2.isEmpty()) return 1.0;
        Set<String> intersection = new HashSet<>(kw1);
        intersection.retainAll(kw2);
        Set<String> union = new HashSet<>(kw1);
        union.addAll(kw2);
        return (double) intersection.size() / union.size();
    }

    /**
     * Check if input is a question.
     */
    public boolean isQuestion(String text) {
        String t = text.trim().toLowerCase();
        return t.endsWith("?") ||
                t.startsWith("what") || t.startsWith("how") || t.startsWith("why") ||
                t.startsWith("when") || t.startsWith("where") || t.startsWith("who") ||
                t.startsWith("which") || t.startsWith("is ") || t.startsWith("are ") ||
                t.startsWith("can ") || t.startsWith("does ") || t.startsWith("do ");
    }

    /**
     * Extract numbers from text.
     */
    public List<Integer> extractNumbers(String text) {
        List<Integer> numbers = new ArrayList<>();
        Matcher m = Pattern.compile("\\b\\d+\\b").matcher(text);
        while (m.find()) {
            try { numbers.add(Integer.parseInt(m.group())); } catch (NumberFormatException ignored) {}
        }
        return numbers;
    }

    /**
     * Calculate text complexity score (0-1).
     */
    public double getComplexityScore(String text) {
        List<String> tokens = tokenize(text);
        if (tokens.isEmpty()) return 0;
        double avgWordLength = tokens.stream().mapToInt(String::length).average().orElse(0);
        List<String> sentences = sentenceSplit(text);
        double avgSentenceLength = sentences.isEmpty() ? 0 : (double) tokens.size() / sentences.size();
        return Math.min(1.0, (avgWordLength / 10.0 + avgSentenceLength / 30.0) / 2.0);
    }
}
