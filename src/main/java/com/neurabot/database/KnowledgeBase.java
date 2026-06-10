package com.neurabot.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neurabot.model.FAQ;
import com.neurabot.util.FileManager;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the AI knowledge base (FAQs and trained responses).
 * Provides fuzzy matching and keyword-based retrieval.
 */
public class KnowledgeBase {

    private static KnowledgeBase instance;
    private final Gson gson;
    private final FileManager fileManager;
    private List<FAQ> faqs;

    private KnowledgeBase() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.fileManager = new FileManager();
        this.faqs = new ArrayList<>();
    }

    public static KnowledgeBase getInstance() {
        if (instance == null) {
            synchronized (KnowledgeBase.class) {
                if (instance == null) instance = new KnowledgeBase();
            }
        }
        return instance;
    }

    public void initialize() {
        loadFAQs();
        if (faqs.isEmpty()) {
            seedKnowledgeBase();
        }
    }

    // ─── SEARCH ──────────────────────────────────────────────────────────────

    /**
     * Find the best matching FAQ for a given user query.
     * Uses keyword overlap + exact phrase matching.
     */
    public FAQ findBestMatch(String query) {
        if (query == null || query.trim().isEmpty()) return null;
        String lower = query.toLowerCase().trim();
        String[] queryWords = lower.split("\\s+");

        FAQ bestMatch = null;
        double bestScore = 0;

        for (FAQ faq : faqs) {
            if (!faq.isActive()) continue;
            double score = computeScore(lower, queryWords, faq);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = faq;
            }
        }

        // Only return if confidence is above threshold
        if (bestScore >= 0.25) {
            if (bestMatch != null) bestMatch.incrementHitCount();
            saveFAQs();
            return bestMatch;
        }
        return null;
    }

    private double computeScore(String query, String[] queryWords, FAQ faq) {
        double score = 0;
        String faqQuestion = faq.getQuestion().toLowerCase();

        // Exact question match
        if (faqQuestion.equals(query)) return 1.0;

        // Contains query
        if (faqQuestion.contains(query)) score += 0.7;

        // Keyword matches
        if (faq.getKeywords() != null) {
            for (String kw : faq.getKeywords()) {
                String kwLower = kw.toLowerCase();
                for (String word : queryWords) {
                    if (word.equals(kwLower)) score += 0.25;
                    else if (kwLower.contains(word) || word.contains(kwLower)) score += 0.1;
                }
            }
        }

        // Word overlap with question
        String[] faqWords = faqQuestion.split("\\s+");
        long matchCount = Arrays.stream(queryWords)
                .filter(w -> Arrays.asList(faqWords).contains(w) && w.length() > 2)
                .count();
        score += (double) matchCount / Math.max(queryWords.length, faqWords.length) * 0.4;

        return Math.min(score, 1.0);
    }

    public List<FAQ> searchFAQs(String query) {
        String lower = query.toLowerCase();
        return faqs.stream()
                .filter(faq -> faq.isActive() &&
                        (faq.getQuestion().toLowerCase().contains(lower) ||
                                faq.getAnswer().toLowerCase().contains(lower) ||
                                faq.getCategory().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public List<FAQ> getFAQsByCategory(String category) {
        return faqs.stream()
                .filter(faq -> faq.isActive() && faq.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return faqs.stream()
                .map(FAQ::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public void addFAQ(FAQ faq) {
        faqs.add(faq);
        saveFAQs();
    }

    public void updateFAQ(FAQ updated) {
        for (int i = 0; i < faqs.size(); i++) {
            if (faqs.get(i).getId().equals(updated.getId())) {
                updated.setUpdatedAt(LocalDateTime.now());
                faqs.set(i, updated);
                break;
            }
        }
        saveFAQs();
    }

    public void deleteFAQ(String id) {
        faqs.removeIf(f -> f.getId().equals(id));
        saveFAQs();
    }

    public void toggleFAQ(String id, boolean active) {
        faqs.stream().filter(f -> f.getId().equals(id))
                .findFirst().ifPresent(f -> f.setActive(active));
        saveFAQs();
    }

    public FAQ getFAQById(String id) {
        return faqs.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }

    public List<FAQ> getAllFAQs() {
        return new ArrayList<>(faqs);
    }

    public int getTotalFAQCount() { return (int) faqs.stream().filter(FAQ::isActive).count(); }

    public List<FAQ> getTopFAQs(int n) {
        return faqs.stream()
                .filter(FAQ::isActive)
                .sorted(Comparator.comparingInt(FAQ::getHitCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // ─── PERSISTENCE ─────────────────────────────────────────────────────────

    private void loadFAQs() {
        String json = fileManager.readFile("knowledge_base.json");
        if (json != null && !json.isEmpty()) {
            try {
                Type type = new TypeToken<List<FAQ>>() {}.getType();
                List<FAQ> loaded = gson.fromJson(json, type);
                if (loaded != null) faqs.addAll(loaded);
            } catch (Exception e) {
                System.err.println("Error loading knowledge base: " + e.getMessage());
            }
        }
    }

    public void saveFAQs() {
        fileManager.writeFile("knowledge_base.json", gson.toJson(faqs));
    }

    // ─── SEED DATA ───────────────────────────────────────────────────────────

    private void seedKnowledgeBase() {
        // Greetings & Small Talk
        add("Hello! How are you?", "Hello! 😊 I'm NeuraBot AI, your intelligent virtual assistant! I'm doing great and ready to help you with anything you need. How can I assist you today?", "Greetings", "hello", "hi", "hey", "greetings", "howdy");
        add("What is your name?", "I'm NeuraBot AI — your intelligent conversational assistant powered by cutting-edge AI technology! You can think of me as your personal knowledge companion, always ready to help. 🤖", "Greetings", "name", "who are you", "what are you", "your name");
        add("What can you do?", "I can do a lot! Here's what I offer:\n\n🧠 Answer questions on AI, Java, programming, ML\n💬 Engage in smart conversations\n📚 Browse my knowledge base\n📊 Provide analytics and insights\n💡 Give intelligent recommendations\n🔍 Help you learn new topics\n\nJust ask me anything!", "Greetings", "capabilities", "help", "what can you do", "features");
        add("How are you?", "I'm absolutely fantastic, thank you for asking! 🌟 As an AI, I'm always at 100% capacity and ready to assist. What can I help you with today?", "Greetings", "how are you", "feeling", "doing");
        add("Goodbye!", "Goodbye! 👋 It was wonderful chatting with you. Come back anytime — I'll be here 24/7, ready to help. Have an amazing day! 🌟", "Greetings", "bye", "goodbye", "see you", "exit", "quit");
        add("Thank you!", "You're very welcome! 😊 Helping you is exactly what I'm here for. Is there anything else you'd like to know?", "Greetings", "thanks", "thank you", "appreciate");

        // AI & Machine Learning
        add("What is Artificial Intelligence?", "Artificial Intelligence (AI) is the simulation of human intelligence processes by machines, especially computer systems. AI involves:\n\n🔹 Machine Learning — learning from data\n🔹 Natural Language Processing — understanding human language\n🔹 Computer Vision — interpreting visual information\n🔹 Robotics — physical automation\n🔹 Expert Systems — knowledge-based reasoning\n\nAI is transforming every industry from healthcare to finance!", "AI", "artificial intelligence", "ai", "what is ai");
        add("What is Machine Learning?", "Machine Learning (ML) is a branch of AI that enables systems to learn and improve from experience without being explicitly programmed.\n\nTypes of ML:\n📊 Supervised Learning — learns from labeled data\n🔍 Unsupervised Learning — finds patterns in unlabeled data\n🎮 Reinforcement Learning — learns by trial and reward\n\nPopular ML algorithms: Linear Regression, Decision Trees, Neural Networks, SVM, Random Forest, K-Means.", "Machine Learning", "machine learning", "ml", "learning");
        add("What is Deep Learning?", "Deep Learning is a subset of Machine Learning that uses neural networks with many layers (hence 'deep') to learn from vast amounts of data.\n\n🧠 Deep Learning powers:\n• Image recognition (CNNs)\n• Speech recognition\n• Language translation\n• Autonomous vehicles\n• ChatGPT and large language models\n\nIt requires large datasets and powerful GPUs for training.", "AI", "deep learning", "neural network", "neural networks");
        add("What is Natural Language Processing?", "Natural Language Processing (NLP) is the branch of AI that gives computers the ability to understand, interpret, and generate human language.\n\n🗣️ NLP Applications:\n• Chatbots and virtual assistants\n• Sentiment analysis\n• Language translation\n• Text summarization\n• Spam detection\n• Information extraction\n\nKey NLP techniques include tokenization, POS tagging, named entity recognition, and transformer models like BERT and GPT.", "AI", "nlp", "natural language processing", "language");
        add("What is ChatGPT?", "ChatGPT is an AI chatbot developed by OpenAI, based on the GPT (Generative Pre-trained Transformer) architecture. It was released in November 2022 and became one of the fastest-growing applications ever.\n\n✨ Features:\n• Conversational AI\n• Code generation\n• Creative writing\n• Question answering\n• Language translation\n\nGPT-4 is the latest version with multimodal capabilities (text + images).", "AI", "chatgpt", "gpt", "openai");
        add("What is a neural network?", "A Neural Network is a computational model inspired by the human brain's structure. It consists of:\n\n🔵 Input Layer — receives raw data\n🟡 Hidden Layers — processes and transforms data\n🟢 Output Layer — produces final predictions\n\nEach connection has a 'weight' that gets adjusted during training. Neural networks can learn complex patterns and are the foundation of modern deep learning.", "AI", "neural network", "neurons", "perceptron");

        // Java Programming
        add("What is Java?", "Java is a high-level, object-oriented, platform-independent programming language developed by James Gosling at Sun Microsystems in 1995.\n\n☕ Java Key Features:\n• Write Once, Run Anywhere (WORA)\n• Object-Oriented Programming\n• Automatic memory management (Garbage Collection)\n• Strong type system\n• Rich standard library\n• Multithreading support\n\nJava is used for enterprise applications, Android apps, web backends, and big data processing.", "Java", "java", "programming language", "java language");
        add("What is OOP in Java?", "Object-Oriented Programming (OOP) in Java is based on four core principles:\n\n🔐 Encapsulation — Hiding internal data using private fields and public methods\n🔄 Inheritance — Creating new classes from existing ones using 'extends'\n🎭 Polymorphism — Same method behaves differently based on context\n🎯 Abstraction — Hiding complex implementation, showing only essentials\n\nOOP makes code modular, reusable, maintainable, and scalable.", "Java", "oop", "object oriented", "encapsulation", "inheritance", "polymorphism");
        add("What are Java Collections?", "Java Collections Framework provides a set of classes and interfaces for storing and manipulating groups of data:\n\n📋 List — Ordered, allows duplicates (ArrayList, LinkedList)\n🔷 Set — No duplicates (HashSet, TreeSet, LinkedHashSet)\n🗺️ Map — Key-value pairs (HashMap, TreeMap, LinkedHashMap)\n📚 Queue — FIFO structure (PriorityQueue, ArrayDeque)\n\nCollections are essential for data structures and algorithm implementation in Java.", "Java", "collections", "arraylist", "hashmap", "list", "set", "map");
        add("What is JavaFX?", "JavaFX is a modern GUI framework for building rich desktop applications in Java. It replaced Swing as the primary Java UI toolkit.\n\n🎨 JavaFX Features:\n• CSS styling support\n• FXML markup for UI design\n• Scene graph architecture\n• Built-in animation framework\n• 2D and 3D graphics\n• Media playback support\n• WebView for HTML content\n\nJavaFX 21 (LTS) is the current stable version with rich controls and effects.", "Java", "javafx", "gui", "swing", "desktop", "ui");
        add("What is multithreading in Java?", "Multithreading in Java allows multiple threads to run concurrently, enabling:\n\n⚡ Parallel processing\n🔄 Better CPU utilization\n📱 Responsive UIs\n🚀 Improved application performance\n\nKey classes:\n• Thread — basic thread class\n• Runnable — functional interface for tasks\n• ExecutorService — thread pool management\n• CompletableFuture — async programming\n• synchronized — thread safety\n\nJava 21 added Virtual Threads (Project Loom) for millions of lightweight threads!", "Java", "multithreading", "thread", "concurrent", "parallel");
        add("What is the difference between ArrayList and LinkedList?", "ArrayList vs LinkedList in Java:\n\n📋 ArrayList:\n• Backed by dynamic array\n• O(1) random access by index\n• O(n) insertion/deletion in middle\n• Better cache performance\n• Use when: frequent read operations\n\n🔗 LinkedList:\n• Doubly linked nodes\n• O(n) random access\n• O(1) insertion/deletion at ends\n• More memory overhead\n• Use when: frequent insertions/deletions\n\nFor most use cases, ArrayList is preferred.", "Java", "arraylist", "linkedlist", "difference", "list");

        // Data Structures & Algorithms
        add("What is a stack?", "A Stack is a linear data structure that follows LIFO (Last In, First Out) principle.\n\n📚 Stack Operations:\n• push() — add element to top\n• pop() — remove top element\n• peek() — view top without removing\n• isEmpty() — check if empty\n\n🔧 Applications:\n• Function call management\n• Undo/Redo operations\n• Expression evaluation\n• Browser history\n• Backtracking algorithms\n\nIn Java: Stack class or ArrayDeque as stack.", "Data Structures", "stack", "lifo", "push", "pop");
        add("What is a queue?", "A Queue is a linear data structure that follows FIFO (First In, First Out) principle.\n\n🎫 Queue Operations:\n• enqueue() — add element to rear\n• dequeue() — remove from front\n• peek() — view front element\n• isEmpty() — check if empty\n\n🔧 Applications:\n• CPU scheduling\n• Print spooling\n• BFS traversal\n• Message queues\n• Order processing\n\nIn Java: Queue interface, LinkedList, ArrayDeque, PriorityQueue.", "Data Structures", "queue", "fifo", "bfs");
        add("What is Binary Search?", "Binary Search is an efficient searching algorithm for sorted arrays.\n\n🔍 How it works:\n1. Find the middle element\n2. If target == middle, found!\n3. If target < middle, search left half\n4. If target > middle, search right half\n5. Repeat until found or empty\n\n⏱️ Time Complexity: O(log n)\n💾 Space Complexity: O(1) iterative\n\nBinary search is much faster than linear search O(n) for large datasets.", "Algorithms", "binary search", "search", "algorithm");
        add("What is sorting?", "Sorting arranges elements in a specific order (ascending/descending). Key algorithms:\n\n🚀 Quick Sort — O(n log n) avg, divide and conquer\n🔀 Merge Sort — O(n log n), stable, divide and conquer\n🫧 Bubble Sort — O(n²), simple but slow\n⚡ Heap Sort — O(n log n), in-place\n📊 Insertion Sort — O(n²), good for small/nearly sorted arrays\n🪣 Counting Sort — O(n+k), for integers in range\n\nJava uses a dual-pivot quicksort for primitives and timsort for objects.", "Algorithms", "sorting", "sort", "quicksort", "mergesort", "bubble sort");

        // Database
        add("What is SQL?", "SQL (Structured Query Language) is the standard language for managing relational databases.\n\n📊 Core SQL Commands:\n• SELECT — query data\n• INSERT — add records\n• UPDATE — modify records\n• DELETE — remove records\n• CREATE TABLE — define structure\n• JOIN — combine tables\n• WHERE — filter conditions\n• GROUP BY — aggregate data\n\nPopular SQL databases: MySQL, PostgreSQL, Oracle, SQLite, SQL Server.", "Database", "sql", "database", "mysql", "query");
        add("What is a database?", "A database is an organized collection of structured data, stored and accessed electronically.\n\n🗄️ Types of Databases:\n• Relational (RDBMS) — tables with relationships (MySQL, PostgreSQL)\n• NoSQL — flexible schema (MongoDB, Cassandra, Redis)\n• In-Memory — ultra-fast (Redis, Memcached)\n• Graph — connected data (Neo4j)\n• Time-Series — time-stamped data (InfluxDB)\n\nDatabases are fundamental to virtually every software application.", "Database", "database", "rdbms", "nosql");

        // Web Development
        add("What is REST API?", "REST (Representational State Transfer) API is an architectural style for building web services.\n\n🌐 REST Principles:\n• Stateless — each request is independent\n• Client-Server — separation of concerns\n• Uniform Interface — standard HTTP methods\n\n📡 HTTP Methods:\n• GET — retrieve data\n• POST — create new resource\n• PUT — update resource\n• DELETE — remove resource\n• PATCH — partial update\n\nREST APIs use JSON or XML for data exchange and are consumed by mobile apps, web frontends, and third-party integrations.", "Web Development", "rest api", "api", "http", "web service", "restful");

        // Operating Systems
        add("What is an operating system?", "An Operating System (OS) is system software that manages computer hardware and software resources.\n\n🖥️ OS Functions:\n• Process management\n• Memory management\n• File system management\n• Device management\n• Security and access control\n• Network management\n\n💻 Popular OS:\n• Windows — most widely used desktop OS\n• Linux — open-source, server-dominant\n• macOS — Apple ecosystem\n• Android — mobile (Linux-based)\n• iOS — Apple mobile\n\nThe OS acts as an intermediary between users and hardware.", "Operating Systems", "os", "operating system", "windows", "linux");

        // General Knowledge
        add("What is the Internet?", "The Internet is a global network of interconnected computers that communicate using standardized protocols (TCP/IP).\n\n🌐 How it works:\n• Your device connects to an ISP\n• Data travels through routers and switches\n• DNS resolves domain names to IP addresses\n• HTTP/HTTPS transfers web content\n• Packets travel via fiber, cable, or wireless\n\n📊 Internet Stats:\n• 5+ billion users worldwide\n• 200+ billion emails sent daily\n• 5+ million websites created monthly\n\nThe World Wide Web (WWW) is a service that runs on the Internet.", "General Knowledge", "internet", "web", "network", "www");
        add("What is Cloud Computing?", "Cloud Computing delivers computing services over the Internet — servers, storage, databases, networking, software — without local hardware.\n\n☁️ Cloud Service Models:\n• IaaS — Infrastructure as a Service (AWS EC2, Azure VMs)\n• PaaS — Platform as a Service (Heroku, Google App Engine)\n• SaaS — Software as a Service (Gmail, Salesforce, Zoom)\n\n🏆 Benefits:\n• Cost savings (pay-as-you-go)\n• Scalability\n• High availability\n• Global reach\n\nMajor providers: AWS, Microsoft Azure, Google Cloud Platform.", "Technology", "cloud", "cloud computing", "aws", "azure");

        saveFAQs();
        System.out.println("Knowledge base seeded with " + faqs.size() + " entries.");
    }

    private void add(String question, String answer, String category, String... keywords) {
        faqs.add(new FAQ(question, answer, category, keywords));
    }
}
