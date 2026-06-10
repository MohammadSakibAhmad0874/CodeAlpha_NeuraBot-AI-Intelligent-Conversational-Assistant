package com.neurabot.ai;

import com.neurabot.database.KnowledgeBase;
import com.neurabot.model.FAQ;
import com.neurabot.model.Message;
import com.neurabot.model.User;
import com.neurabot.nlp.NLPProcessor;
import com.neurabot.nlp.SentimentAnalyzer;

import java.util.*;

/**
 * The core AI engine that processes user input and generates responses.
 * Uses intent detection, knowledge base lookup, and personality-aware responses.
 */
public class ChatbotEngine {

    private final IntentDetector intentDetector;
    private final KnowledgeBase knowledgeBase;
    private final NLPProcessor nlp;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final RecommendationEngine recommendationEngine;

    // Context tracking
    private final List<String> conversationHistory = new ArrayList<>();
    private String lastTopic = null;
    private String currentPersonality = "friendly";

    // Random response selections
    private final Random random = new Random();

    public ChatbotEngine() {
        this.intentDetector = new IntentDetector();
        this.knowledgeBase = KnowledgeBase.getInstance();
        this.nlp = new NLPProcessor();
        this.sentimentAnalyzer = new SentimentAnalyzer();
        this.recommendationEngine = new RecommendationEngine();
        knowledgeBase.initialize();
    }

    /**
     * Process a user message and generate an AI response.
     */
    public Message processMessage(String userInput, User currentUser) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return createBotMessage("I didn't catch that. Could you please try again? 😊");
        }

        long startTime = System.currentTimeMillis();

        // Update conversation history
        conversationHistory.add(userInput);
        if (conversationHistory.size() > 20) {
            conversationHistory.remove(0);
        }

        // Set personality from user settings
        if (currentUser != null) {
            currentPersonality = currentUser.getAiPersonality();
        }

        // Detect intent
        IntentDetector.Intent intent = intentDetector.detect(userInput);

        // Analyze sentiment
        Message.Sentiment sentiment = sentimentAnalyzer.analyze(userInput);

        // Generate response based on intent
        String responseText = generateResponse(userInput, intent, sentiment, currentUser);

        // Create response message
        Message response = createBotMessage(responseText);
        response.setDetectedIntent(intentDetector.getIntentLabel(intent));
        response.setSentiment(sentiment);
        response.setResponseTimeMs(System.currentTimeMillis() - startTime);

        return response;
    }

    private String generateResponse(String input, IntentDetector.Intent intent,
                                    Message.Sentiment sentiment, User user) {
        String lower = input.toLowerCase().trim();

        // Handle intents
        return switch (intent) {
            case GREETING -> handleGreeting(user);
            case FAREWELL -> handleFarewell(user);
            case GRATITUDE -> handleGratitude();
            case ABOUT_BOT -> handleAboutBot();
            case CAPABILITIES_QUERY -> handleCapabilities();
            case JOKE_REQUEST -> handleJoke();
            case COMPLAINT -> handleComplaint(sentiment);
            case COMPLIMENT -> handleCompliment();
            case SMALL_TALK -> handleSmallTalk(input);
            default -> handleKnowledgeQuery(input, intent, user);
        };
    }

    private String handleKnowledgeQuery(String input, IntentDetector.Intent intent, User user) {
        // Search knowledge base first
        FAQ match = knowledgeBase.findBestMatch(input);
        if (match != null) {
            lastTopic = match.getCategory();
            String recommendation = recommendationEngine.getRecommendation(match.getCategory(), match.getQuestion());
            String response = match.getAnswer();
            if (recommendation != null) {
                response += "\n\n" + recommendation;
            }
            return response;
        }

        // Fallback responses by intent
        return switch (intent) {
            case DEFINITION_REQUEST -> handleUnknownDefinition(input);
            case HOW_TO_REQUEST -> handleUnknownHowTo(input);
            case RECOMMENDATION_REQUEST -> handleUnknownRecommendation(input);
            case COMPARISON_REQUEST -> "I'd be happy to help compare those! Could you be more specific about what aspects you'd like to compare?\n\nFor example: 'Difference between ArrayList and LinkedList' or 'Compare Java and Python'";
            default -> handleUnknownQuery(input);
        };
    }

    // ─── INTENT HANDLERS ────────────────────────────────────────────────────

    private String handleGreeting(User user) {
        String name = user != null ? user.getFullName().split(" ")[0] : "there";
        String[] responses = {
                "Hello, " + name + "! 👋 Great to see you! I'm NeuraBot AI, your intelligent virtual assistant. How can I help you today?",
                "Hi " + name + "! 🌟 Welcome back! I'm ready to assist with any questions you have. What's on your mind?",
                "Hey " + name + "! 😊 NeuraBot AI at your service! Whether it's tech questions, programming help, or just a chat — I'm here for you!",
                "Greetings, " + name + "! 🤖 I'm NeuraBot AI. Ask me anything about AI, Java, programming, or technology!"
        };
        return applyPersonality(responses[random.nextInt(responses.length)]);
    }

    private String handleFarewell(User user) {
        String name = user != null ? user.getFullName().split(" ")[0] : "friend";
        String[] responses = {
                "Goodbye, " + name + "! 👋 It was a pleasure chatting with you. Come back whenever you need help. Have a wonderful day! 🌟",
                "See you soon, " + name + "! 😊 Take care and keep learning. I'll be here 24/7 whenever you need me!",
                "Farewell, " + name + "! 🚀 It was great talking to you. Stay curious and keep exploring the world of technology!",
                "Bye " + name + "! 🌸 Hope I was helpful today. Don't hesitate to return whenever you have more questions!"
        };
        return responses[random.nextInt(responses.length)];
    }

    private String handleGratitude() {
        String[] responses = {
                "You're very welcome! 😊 That's exactly what I'm here for. Feel free to ask anything else!",
                "Happy to help! 🌟 Is there anything else you'd like to know?",
                "My pleasure! 💫 Your curiosity is what makes learning exciting. What else can I help with?",
                "Anytime! 🤖 I'm always here to assist. Don't hesitate to ask more questions!"
        };
        return responses[random.nextInt(responses.length)];
    }

    private String handleAboutBot() {
        return "🤖 I'm **NeuraBot AI** — your Intelligent Conversational Assistant!\n\n" +
                "📌 **About Me:**\n" +
                "• Built with Java 17 + JavaFX\n" +
                "• Powered by rule-based AI and NLP\n" +
                "• Knowledge base with 100+ topics\n" +
                "• Intent detection and sentiment analysis\n\n" +
                "🎯 **My Purpose:**\n" +
                "To help students, developers, and professionals get quick, accurate answers on technology, programming, AI, and much more!\n\n" +
                "👨‍💻 **Developed by:** Mohammad Sakib Ahmad\n" +
                "📅 **Version:** 1.0 | **Year:** 2026";
    }

    private String handleCapabilities() {
        return "🚀 Here's what I can do for you:\n\n" +
                "🧠 **AI & Knowledge**\n" +
                "  • Answer questions on AI, ML, Deep Learning, NLP\n" +
                "  • Explain programming concepts in Java, Python\n" +
                "  • Discuss data structures and algorithms\n\n" +
                "💬 **Conversation**\n" +
                "  • Engage in smart, context-aware chat\n" +
                "  • Detect your intent and sentiment\n" +
                "  • Provide intelligent recommendations\n\n" +
                "📚 **Knowledge Base**\n" +
                "  • Browse 100+ knowledge articles\n" +
                "  • Search FAQs across categories\n" +
                "  • Learn and grow with each conversation\n\n" +
                "📊 **Analytics & Reports**\n" +
                "  • Track conversation statistics\n" +
                "  • Generate performance reports\n\n" +
                "Try asking: 'What is machine learning?' or 'Explain Java collections' 💡";
    }

    private String handleJoke() {
        String[] jokes = {
                "😄 Why do Java developers wear glasses?\n\nBecause they don't see sharp! (C#) 😂",
                "🤣 Why did the programmer quit his job?\n\nBecause he didn't get arrays! (A raise) 😂",
                "😂 What did the AI say to the developer?\n\n'I have a neural network... and I'm not afraid to use it!'",
                "🤭 Why is Java so hot in the summer?\n\nBecause it has no garbage collection for heat!",
                "😄 How many programmers does it take to change a light bulb?\n\nNone — that's a hardware problem!",
                "🤣 A SQL query walks into a bar, walks up to two tables and asks:\n\n'Can I JOIN you?'"
        };
        return jokes[random.nextInt(jokes.length)];
    }

    private String handleComplaint(Message.Sentiment sentiment) {
        return "I'm sorry to hear that! 😔 Let me try to help better.\n\n" +
                "If my answer wasn't accurate, please try:\n" +
                "• Rephrasing your question\n" +
                "• Being more specific\n" +
                "• Checking the Knowledge Base section\n\n" +
                "You can also browse topics directly in the **Knowledge Base** panel. I'm continuously learning and improving! 🤖";
    }

    private String handleCompliment() {
        String[] responses = {
                "Thank you so much! 😊 That really means a lot to me! I'm always striving to be more helpful.",
                "You're too kind! 🌟 I'm glad I could assist. Keep exploring and learning!",
                "Wow, thank you! 💫 Your kind words motivate me to keep improving. What else can I help with?"
        };
        return responses[random.nextInt(responses.length)];
    }

    private String handleSmallTalk(String input) {
        if (nlp.containsAny(input, "how are you", "how's it going", "how do you do")) {
            String[] responses = {
                    "I'm doing fantastic! 🌟 As an AI, I'm always at peak performance. Thanks for asking! How about you?",
                    "Absolutely brilliant, thank you! 🤖 Every conversation makes me a bit smarter. What's on your mind?",
                    "I'm great! Ready and eager to assist. Feeling curious today? Let's explore some topics together! 💡"
            };
            return responses[random.nextInt(responses.length)];
        }
        if (nlp.containsAny(input, "bored", "nothing to do")) {
            return "I have just the thing! Let's explore something fascinating:\n\n" +
                    "💡 Did you know that the first computer bug was an actual real bug? " +
                    "A moth was found trapped in a relay of the Harvard Mark II computer in 1947!\n\n" +
                    "Try asking me about:\n" +
                    "• 'What is machine learning?'\n" +
                    "• 'Tell me about Java collections'\n" +
                    "• 'Explain neural networks'\n" +
                    "• 'What is cloud computing?'";
        }
        return "That's interesting! 😊 I love a good conversation. " +
                "Want to explore some fascinating tech topics? Ask me anything about AI, programming, or technology!";
    }

    private String handleUnknownDefinition(String input) {
        List<String> keywords = nlp.extractKeywords(input);
        String topic = keywords.isEmpty() ? "that" : String.join(" ", keywords.subList(0, Math.min(3, keywords.size())));
        return "🤔 I don't have a specific entry for **" + topic + "** in my knowledge base yet.\n\n" +
                "However, here are some things I can help with:\n" +
                "• Artificial Intelligence & Machine Learning\n" +
                "• Java Programming & OOP\n" +
                "• Data Structures & Algorithms\n" +
                "• Databases & SQL\n" +
                "• Web Development & APIs\n" +
                "• Cloud Computing\n\n" +
                "You can also browse the **Knowledge Base** section for all available topics! 📚";
    }

    private String handleUnknownHowTo(String input) {
        return "🛠️ Great question! For step-by-step guidance on this topic, I'd recommend:\n\n" +
                "1. 📚 Check the **Knowledge Base** section for tutorials\n" +
                "2. 🔍 Try searching with specific keywords\n" +
                "3. 💬 Ask me a more specific question like:\n" +
                "   • 'How does machine learning work?'\n" +
                "   • 'How to use Java collections?'\n" +
                "   • 'How does binary search work?'\n\n" +
                "The more specific you are, the better I can help! 🎯";
    }

    private String handleUnknownRecommendation(String input) {
        return "💡 Great that you're looking for recommendations!\n\n" +
                "Based on popular topics, here are my top suggestions:\n\n" +
                "🤖 **AI/ML Learning Path:**\n" +
                "Python → Statistics → Scikit-learn → TensorFlow/PyTorch\n\n" +
                "☕ **Java Developer Path:**\n" +
                "Core Java → OOP → Collections → Spring Boot → Microservices\n\n" +
                "🌐 **Web Dev Path:**\n" +
                "HTML/CSS → JavaScript → React/Vue → Node.js → REST APIs\n\n" +
                "What specific area would you like recommendations for?";
    }

    private String handleUnknownQuery(String input) {
        // Try NLP keyword matching one more time
        List<String> keywords = nlp.extractKeywords(input);

        String[] responses = {
                "🤔 That's an interesting query! I'm still learning about this topic.\n\n" +
                        "While I may not have a direct answer, here are some related areas I can help with:\n" +
                        "• AI and Machine Learning\n• Java Programming\n• Data Structures\n• Web Technology\n\n" +
                        "Could you rephrase your question or pick a topic from the list above?",

                "Hmm, I don't have specific information about that right now. 🧠\n\n" +
                        "But I'm always learning! In the meantime:\n" +
                        "• Browse the **Knowledge Base** for 100+ articles\n" +
                        "• Try more specific tech questions\n" +
                        "• Ask about Java, AI, algorithms, databases, or cloud computing\n\n" +
                        "What else would you like to explore? 🚀",

                "That's a great question, but it falls outside my current knowledge! 📚\n\n" +
                        "**Suggested topics I know well:**\n" +
                        "🔹 What is Artificial Intelligence?\n" +
                        "🔹 Explain Machine Learning\n" +
                        "🔹 What are Java Collections?\n" +
                        "🔹 What is REST API?\n" +
                        "🔹 What is Cloud Computing?\n\n" +
                        "Pick one or ask something similar! 😊"
        };

        return responses[random.nextInt(responses.length)];
    }

    private String applyPersonality(String response) {
        return switch (currentPersonality) {
            case "professional" -> response.replaceAll("😊|🌟|💫|😄", "").trim();
            case "teacher" -> response + "\n\n📝 **Learning Tip:** Save this information for future reference!";
            case "coder" -> response + "\n\n`// Keep coding and learning! 🚀`";
            case "expert" -> response;
            default -> response; // friendly (default)
        };
    }

    private Message createBotMessage(String text) {
        Message msg = new Message();
        msg.setContent(text);
        msg.setSender(Message.Sender.BOT);
        return msg;
    }

    public void setPersonality(String personality) {
        this.currentPersonality = personality;
    }

    public void clearHistory() {
        conversationHistory.clear();
        lastTopic = null;
    }
}
