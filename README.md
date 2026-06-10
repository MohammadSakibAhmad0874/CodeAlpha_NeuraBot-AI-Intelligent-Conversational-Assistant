# NeuraBot AI — Intelligent Conversational Assistant

<div align="center">

```
███╗   ██╗███████╗██╗   ██╗██████╗  █████╗ ██████╗  ██████╗ ████████╗
████╗  ██║██╔════╝██║   ██║██╔══██╗██╔══██╗██╔══██╗██╔═══██╗╚══██╔══╝
██╔██╗ ██║█████╗  ██║   ██║██████╔╝███████║██████╔╝██║   ██║   ██║   
██║╚██╗██║██╔══╝  ██║   ██║██╔══██╗██╔══██║██╔══██╗██║   ██║   ██║   
██║ ╚████║███████╗╚██████╔╝██║  ██║██║  ██║██████╔╝╚██████╔╝   ██║   
╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝  ╚═════╝    ╚═╝   
```

### **"Think. Learn. Assist."**

*A professional AI Conversational Assistant powered by Rule-Based NLP & JavaFX*

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-25-blue?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-red?style=for-the-badge&logo=apachemaven&logoColor=white)
![Gson](https://img.shields.io/badge/Gson-2.10.1-brightgreen?style=for-the-badge&logo=google&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)
![Screens](https://img.shields.io/badge/UI_Screens-11-informational?style=for-the-badge)

</div>

---

## 📌 Project Overview

**NeuraBot AI** is a complete, professional-grade AI Conversational Assistant built as a **Final Year B.Tech Computer Science Project**. It demonstrates real-world implementation of:

- ✅ **Natural Language Processing (NLP)** — Tokenization, intent detection, sentiment analysis
- ✅ **Rule-Based AI Engine** — 16 intent categories, fuzzy knowledge matching
- ✅ **Knowledge Base Management** — 30+ seeded FAQ articles, CRUD operations
- ✅ **Premium JavaFX GUI** — 11 animated screens inspired by ChatGPT & Gemini
- ✅ **MVC Architecture** — Clean separation of concerns with design patterns
- ✅ **Data Persistence** — JSON-based storage (Gson) for all app data

> **"NeuraBot AI looks and feels like a commercial AI SaaS product — not a typical college project."**

---

## 👨‍💻 Developer

| Field | Details |
|-------|---------|
| **Name** | Mohammad Sakib Ahmad |
| **Project Type** | Final Year B.Tech CSE Project |
| **Organization** | CodeAlpha Internship — Task 03 |
| **Version** | 1.0 |
| **Year** | 2025–2026 |

---

## 🚀 Key Features

### 🤖 AI & NLP Engine
- **16-class Intent Detection** — Greeting, Question, Farewell, SmallTalk, Help, Joke, Compliment, Complaint, etc.
- **3-level Sentiment Analysis** — Positive / Neutral / Negative with lexicon-based scoring
- **Keyword Extraction** — Stop-word filtering, fuzzy matching with confidence threshold (≥ 0.25)
- **5 AI Personalities** — Friendly, Professional, Teacher, Coder, Expert
- **30+ Knowledge Base Articles** — Pre-seeded across 10 categories

### 💬 Premium Chat Interface
- Animated message bubbles with **FadeTransition + TranslateTransition**
- **Typing indicator** with bouncing animated dots
- Conversation **session history** with sidebar navigation
- Smart **suggestion chips** for guided interaction
- Real-time **sentiment display** in status bar

### 📚 Knowledge Base System
- **10 categories**: AI, Java, Algorithms, Machine Learning, Database, OS, Networks, DSA, Python, General
- Admin CRUD — Add / Edit / Delete FAQs
- Instant search with live filtering
- Hit count tracking for analytics

### 📊 Analytics Dashboard
- **Bar chart** — 7-day conversation volume (custom canvas rendering)
- **Pie chart** — Sentiment distribution (Positive / Neutral / Negative)
- **Top Topics** leaderboard with progress bars
- Export reports to **TXT** and **CSV**

### 🔒 Authentication System
- **SHA-256** password hashing
- Role-based access — **Admin** vs **User**
- Animated login with shake effect on error
- Session tracking with activity logs

### 👑 Admin Panel
- User management — Promote / Delete accounts
- **Bot Training** — Add Q&A pairs to live knowledge base
- **Activity Log viewer** with real-time search

### ⚙ Settings & Customization
- **Dark / Light theme** — instant switch with Observer pattern
- AI Personality selector
- Font size slider (12–20px)
- Animation speed control
- Auto-save, notifications, language toggle

---

## 🛠 Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Language | **Java 25** | Core application logic |
| GUI Framework | **JavaFX 25** | All UI screens & animations |
| JSON Serialization | **Google Gson 2.10.1** | Data persistence (users, sessions, FAQs) |
| Embedded DB | **SQLite JDBC 3.45** | Supplementary database support |
| String Utilities | **Apache Commons Lang 3.14** | Text processing |
| File Utilities | **Apache Commons IO 2.15** | File management |
| Security | **SHA-256 (MessageDigest)** | Password hashing |
| Build Tool | **Apache Maven 3.9** | Dependency management & build |

---

## 📁 Complete Project Structure

```
CodeAlpha_NeuraBot-AI-Intelligent-Conversational-Assistant/
│
├── 📄 pom.xml                          # Maven build configuration (Java 25 + JavaFX 25)
├── 📄 README.md                        # This file
├── 📄 run.bat                          # One-click Windows launcher
│
├── 📁 docs/                            # All project documentation
│   ├── 📄 VIVAQuestions.md             # 23 VIVA Q&A for project defense
│   ├── 📄 UserManual.md                # Complete user guide (all 11 screens)
│   ├── 📄 InstallationGuide.md         # Windows/macOS/Linux setup guide
│   ├── 📄 UMLDiagrams.md               # Class, Use Case, Sequence, ER, Component diagrams
│   ├── 📄 TestingReport.md             # 92 test cases — 98.9% pass rate
│   ├── 📄 FutureScope.md               # Versioned roadmap v2.0 → Enterprise
│   └── 📄 PresentationContent.md       # 12 slides + speaker notes
│
└── 📁 src/main/
    ├── 📁 java/com/neurabot/
    │   │
    │   ├── 📄 App.java                  # JavaFX entry point, ThemeManager init
    │   │
    │   ├── 📁 model/                    # Data models (POJOs)
    │   │   ├── 📄 User.java             # User entity with SHA-256 auth
    │   │   ├── 📄 Message.java          # Chat message (Sender + Sentiment enums)
    │   │   ├── 📄 ChatSession.java      # Conversation session management
    │   │   ├── 📄 FAQ.java              # Knowledge base article
    │   │   ├── 📄 ActivityLog.java      # Audit log with LogType enum
    │   │   └── 📄 UserSettings.java     # Per-user preferences
    │   │
    │   ├── 📁 ai/                       # AI Engine
    │   │   ├── 📄 ChatbotEngine.java    # Core AI: intent → KB match → response
    │   │   ├── 📄 IntentDetector.java   # 16-class pattern-matching intent classifier
    │   │   └── 📄 RecommendationEngine.java  # Topic suggestion engine
    │   │
    │   ├── 📁 nlp/                      # Natural Language Processing
    │   │   ├── 📄 NLPProcessor.java     # Tokenizer + keyword extractor
    │   │   └── 📄 SentimentAnalyzer.java     # Lexicon-based sentiment (Pos/Neu/Neg)
    │   │
    │   ├── 📁 database/                 # Data persistence layer
    │   │   ├── 📄 DatabaseManager.java  # Singleton JSON store (users, sessions, logs)
    │   │   ├── 📄 KnowledgeBase.java    # Singleton FAQ store with fuzzy matching
    │   │   └── 📄 LocalDateTimeAdapter.java  # Gson adapter for java.time.LocalDateTime
    │   │
    │   ├── 📁 analytics/                # Analytics engine
    │   │   └── 📄 AnalyticsManager.java # Aggregates stats, top topics, satisfaction %
    │   │
    │   ├── 📁 reports/                  # Report generation
    │   │   └── 📄 ReportGenerator.java  # TXT + CSV export (user & AI performance)
    │   │
    │   ├── 📁 util/                     # Shared utilities
    │   │   ├── 📄 ThemeManager.java     # Observer pattern, dark/light CSS switching
    │   │   ├── 📄 NotificationManager.java   # Toast-style popup notifications
    │   │   └── 📄 FileManager.java      # ~/.neurabot/ directory management
    │   │
    │   └── 📁 view/                     # JavaFX UI screens (11 screens)
    │       ├── 📄 SplashScreen.java     # Canvas neural particle animation
    │       ├── 📄 LandingPage.java      # Marketing landing with floating particles
    │       ├── 📄 LoginScreen.java      # Split-panel with glassmorphism + shake
    │       ├── 📄 RegisterScreen.java   # Full-validation registration form
    │       ├── 📄 MainDashboard.java    # Hub: 5 stat cards + quick actions
    │       ├── 📄 ChatInterface.java    # Full AI chat: bubbles, typing dots, history
    │       ├── 📄 KnowledgeBaseView.java     # Expandable FAQ cards + search + CRUD
    │       ├── 📄 AnalyticsView.java    # Charts (bar/pie) + top topics + export
    │       ├── 📄 AdminPanel.java       # Users table + bot training + activity log
    │       ├── 📄 SettingsView.java     # Theme, personality, sliders
    │       └── 📄 AboutView.java        # Pulsing icon, tech stack, future scope
    │
    └── 📁 resources/
        └── 📁 styles/
            ├── 📄 dark-theme.css        # Complete dark mode JavaFX CSS
            └── 📄 light-theme.css       # Complete light mode JavaFX CSS
```

---

## ⚡ Quick Start

### Prerequisites

| Requirement | Version | Check Command |
|-------------|---------|--------------|
| Java JDK | **21 or 25 (LTS recommended)** | `java -version` |
| Apache Maven | **3.8+** | `mvn -version` |

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/MohammadSakibAhmad0874/CodeAlpha_NeuraBot-AI-Intelligent-Conversational-Assistant.git
cd CodeAlpha_NeuraBot-AI-Intelligent-Conversational-Assistant

# 2. Build the project
mvn clean package -DskipTests

# 3. Run the application
mvn javafx:run
```

### Windows — One-Click Launch

If your system has **Java 25** installed at `C:\Program Files\Java\jdk-25`:

```bash
# Just double-click run.bat
# OR from terminal:
.\run.bat
```

### Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Full Administrator |
| `demo` | `demo123` | Regular User |

---

## 📊 Application Flow

```
App Launch
    ↓
SplashScreen  ──── (2.5s neural particle animation)
    ↓
LandingPage   ──── (Hero + Features + Stats + Footer)
    ↓
LoginScreen / RegisterScreen
    ↓
MainDashboard  ──── (Central hub with 5 stat cards)
    │
    ├──▶ ChatInterface      ←→ ChatbotEngine ←→ KnowledgeBase
    ├──▶ KnowledgeBaseView  ←→ DatabaseManager
    ├──▶ AnalyticsView      ←→ AnalyticsManager → Reports
    ├──▶ AdminPanel         ←→ DatabaseManager + KnowledgeBase
    ├──▶ SettingsView       ←→ ThemeManager
    └──▶ AboutView
```

---

## 🧠 NLP Pipeline

```
User Input Text
      │
      ▼
 NLPProcessor.tokenize()         →  ["what", "is", "machine", "learning"]
      │
      ▼
 NLPProcessor.extractKeywords()  →  ["machine", "learning"]  (stop-words removed)
      │
      ▼
 IntentDetector.detect()         →  IntentType.DEFINITION_REQUEST
      │
      ▼
 SentimentAnalyzer.analyze()     →  Sentiment.POSITIVE / NEUTRAL / NEGATIVE
      │
      ▼
 KnowledgeBase.findBestMatch()   →  FAQ match (confidence ≥ 0.25)
      │
      ▼
 ChatbotEngine.generateResponse() →  Apply personality mode
      │
      ▼
 Bot Response  →  Displayed with FadeIn animation
```

---

## 💾 Data Storage

All data auto-saved to `~/.neurabot/` in the user's home directory:

| File | Contents |
|------|----------|
| `data/users.json` | Registered user accounts (SHA-256 hashed passwords) |
| `data/sessions.json` | All conversation sessions & messages |
| `data/knowledge_base.json` | FAQs with hit counts and categories |
| `data/activity_logs.json` | Complete audit trail |
| `data/settings.json` | Per-user theme, personality, font preferences |
| `reports/*.txt` | Generated performance reports |
| `reports/*.csv` | Exported conversation & FAQ data |

---

## 🎨 UI Screens (11 Total)

| # | Screen | Highlights |
|---|--------|-----------|
| 1 | **SplashScreen** | Canvas AnimationTimer, neural particles, progress bar |
| 2 | **LandingPage** | Floating particles, stats counters, feature cards |
| 3 | **LoginScreen** | Glassmorphism panel, shake animation, show/hide password |
| 4 | **RegisterScreen** | Real-time validation, strength indicator |
| 5 | **MainDashboard** | 5 stat cards, recent chats, quick action buttons |
| 6 | **ChatInterface** | Bubble animations, typing dots, session sidebar |
| 7 | **KnowledgeBaseView** | Expandable cards, category filter, inline search |
| 8 | **AnalyticsView** | Custom bar/pie charts on Canvas, export buttons |
| 9 | **AdminPanel** | TableView users, bot training tab, log viewer |
| 10 | **SettingsView** | Theme toggle, sliders, personality cards |
| 11 | **AboutView** | Pulsing bot icon, tech stack badges, version info |

---

## 📐 Design Patterns Used

| Pattern | Implementation |
|---------|---------------|
| **Singleton** | `DatabaseManager`, `KnowledgeBase` |
| **Observer** | `ThemeManager` notifies all registered scenes |
| **MVC** | View (JavaFX) → AI Engine → Database (JSON) |
| **Strategy** | AI personality modes (Friendly/Professional/Teacher/Coder/Expert) |
| **Factory** | `NotificationManager.showToast()` creates styled popups |

---

## 📈 OOP Principles Applied

- **Encapsulation** — Private fields + public getters/setters in all 6 model classes
- **Abstraction** — AI engine, NLP, and UI completely decoupled
- **Polymorphism** — `Message.Sender`, `ActivityLog.LogType`, `Message.Sentiment` enums
- **Inheritance** — JavaFX scene graph hierarchy, stage management

---

## 📦 Build & Compile Stats

| Metric | Value |
|--------|-------|
| Java Source Files | **31 classes** |
| UI Screens | **11 screens** |
| Knowledge Articles | **30+ pre-seeded** |
| Intent Categories | **16** |
| Test Cases | **92 manual tests** |
| Test Pass Rate | **98.9%** |
| Shaded JAR Size | **~25 MB** |
| Build Time | **~10 seconds** |

---

## 📚 Documentation

All documentation available in the `/docs/` folder:

| Document | Description |
|----------|-------------|
| [`VIVAQuestions.md`](docs/VIVAQuestions.md) | 23 VIVA Q&A for project defense |
| [`UserManual.md`](docs/UserManual.md) | Step-by-step guide for all 11 screens |
| [`InstallationGuide.md`](docs/InstallationGuide.md) | Setup for Windows / macOS / Linux |
| [`UMLDiagrams.md`](docs/UMLDiagrams.md) | Class, Use Case, Sequence, ER, Component diagrams |
| [`TestingReport.md`](docs/TestingReport.md) | 92 test cases with results |
| [`FutureScope.md`](docs/FutureScope.md) | Roadmap v2.0 → v4.0 → Enterprise |
| [`PresentationContent.md`](docs/PresentationContent.md) | 12-slide deck with speaker notes |

---

## 🔮 Future Scope

| Version | Feature |
|---------|---------|
| **v2.0** | GPT-4 / Gemini API integration for LLM-powered responses |
| **v2.0** | Real-time voice input (Speech-to-Text) + TTS output |
| **v2.1** | Spring Boot REST backend + React.js web frontend |
| **v2.1** | Android mobile app (Kotlin + Material Design 3) |
| **v2.2** | Cloud deployment (AWS/GCP) + Docker + Kubernetes |
| **v3.0** | Fine-tuned BERT model for intent classification |
| **v3.0** | Multi-language NLP (Hindi, Spanish, French, Arabic) |
| **Enterprise** | Team workspaces, SSO/SAML, white-labeling, Slack integration |

---

## 📄 License

```
MIT License — © 2026 Mohammad Sakib Ahmad

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software to use, copy, modify, merge, publish, and distribute, subject
to the following conditions: The above copyright notice shall be included in
all copies. THE SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND.
```

This project was developed for academic purposes as a Final Year B.Tech Computer Science Project and CodeAlpha Internship — Task 03.

---

<div align="center">

**⭐ If this project helped you, please give it a star! ⭐**

Made with ❤️ by **Mohammad Sakib Ahmad**

🤖 **NeuraBot AI — Think. Learn. Assist.**

</div>
