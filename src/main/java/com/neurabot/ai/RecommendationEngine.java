package com.neurabot.ai;

import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.FAQ;

import java.util.*;

/**
 * Provides intelligent topic recommendations based on conversation context.
 */
public class RecommendationEngine {

    // Related topics mapping: category → related suggestions
    private final Map<String, List<String>> relatedTopics = new HashMap<>() {{
        put("AI", Arrays.asList(
                "Would you like to learn about **Machine Learning**?",
                "Related: **Deep Learning and Neural Networks**",
                "Explore: **Natural Language Processing (NLP)**",
                "Next: **AI Applications in Industry**"
        ));
        put("Machine Learning", Arrays.asList(
                "Related: **Deep Learning** and neural networks",
                "Explore: **Python libraries** for ML (TensorFlow, PyTorch)",
                "Next topic: **Supervised vs Unsupervised Learning**"
        ));
        put("Java", Arrays.asList(
                "Related: **Java Collections Framework**",
                "Explore: **Object-Oriented Programming principles**",
                "Next: **JavaFX for modern GUI development**",
                "Advanced: **Java Multithreading and Concurrency**"
        ));
        put("Data Structures", Arrays.asList(
                "Related: **Sorting Algorithms**",
                "Explore: **Binary Search Tree**",
                "Next: **Graph Data Structures**"
        ));
        put("Algorithms", Arrays.asList(
                "Related: **Dynamic Programming**",
                "Explore: **Graph Algorithms (BFS/DFS)**",
                "Next: **Complexity Analysis (Big O Notation)**"
        ));
        put("Database", Arrays.asList(
                "Related: **SQL Joins and Queries**",
                "Explore: **NoSQL Databases (MongoDB)**",
                "Next: **Database Normalization**"
        ));
        put("Web Development", Arrays.asList(
                "Related: **REST API Design**",
                "Explore: **Frontend Frameworks (React, Angular)**",
                "Next: **HTTP Protocol and Web Security**"
        ));
        put("Technology", Arrays.asList(
                "Related: **Cloud Computing (AWS, Azure)**",
                "Explore: **DevOps and CI/CD pipelines**",
                "Next: **Microservices Architecture**"
        ));
        put("Operating Systems", Arrays.asList(
                "Related: **Process Scheduling Algorithms**",
                "Explore: **Memory Management**",
                "Next: **File Systems and I/O**"
        ));
    }};

    private final Random random = new Random();
    private String lastCategory = null;

    /**
     * Get a recommendation based on the current topic.
     */
    public String getRecommendation(String category, String currentQuestion) {
        if (category == null) return null;

        // Don't recommend the same category twice in a row
        if (category.equals(lastCategory) && random.nextDouble() < 0.5) return null;
        lastCategory = category;

        List<String> suggestions = relatedTopics.get(category);
        if (suggestions == null || suggestions.isEmpty()) return null;

        // Only show recommendation 60% of the time to avoid being annoying
        if (random.nextDouble() > 0.6) return null;

        String suggestion = suggestions.get(random.nextInt(suggestions.size()));
        return "💡 **You might also enjoy:** " + suggestion;
    }

    /**
     * Get smart question suggestions for the suggestion bar.
     */
    public List<String> getSuggestedQuestions(String lastTopic) {
        List<String> suggestions = new ArrayList<>();

        if (lastTopic != null) {
            switch (lastTopic) {
                case "AI" -> suggestions.addAll(Arrays.asList(
                        "What is machine learning?", "What is deep learning?", "Explain neural networks"
                ));
                case "Java" -> suggestions.addAll(Arrays.asList(
                        "What are Java Collections?", "Explain OOP in Java", "What is multithreading?"
                ));
                case "Database" -> suggestions.addAll(Arrays.asList(
                        "What is SQL?", "NoSQL vs SQL?", "What is database normalization?"
                ));
            }
        }

        // Always include general suggestions
        suggestions.addAll(Arrays.asList(
                "What is Artificial Intelligence?",
                "Explain Machine Learning",
                "What can you do?",
                "Tell me about Java",
                "What is a neural network?",
                "What is Cloud Computing?",
                "Explain REST API"
        ));

        // Shuffle and return top 5
        Collections.shuffle(suggestions);
        return suggestions.subList(0, Math.min(5, suggestions.size()));
    }

    /**
     * Get default suggestion chips for the chat interface.
     */
    public List<String> getDefaultSuggestions() {
        return Arrays.asList(
                "🤖 What can you do?",
                "🧠 What is AI?",
                "☕ Tell me about Java",
                "🔧 Explain algorithms",
                "💡 What is ML?"
        );
    }
}
