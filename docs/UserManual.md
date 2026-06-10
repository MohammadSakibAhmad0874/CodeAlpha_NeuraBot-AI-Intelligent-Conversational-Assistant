# NeuraBot AI — User Manual
## Version 1.0 | Mohammad Sakib Ahmad

---

## 📖 Table of Contents

1. [Getting Started](#getting-started)
2. [Installation](#installation)
3. [First Launch](#first-launch)
4. [Authentication](#authentication)
5. [Main Dashboard](#main-dashboard)
6. [Chat Interface](#chat-interface)
7. [Knowledge Base](#knowledge-base)
8. [Analytics & Reports](#analytics--reports)
9. [Settings](#settings)
10. [Admin Panel](#admin-panel)
11. [Troubleshooting](#troubleshooting)

---

## 🚀 Getting Started

NeuraBot AI is an AI-powered conversational assistant. It understands your questions, searches its knowledge base, and provides intelligent answers — all through a beautiful, modern interface.

**What you can do:**
- Chat with the AI about Technology, Java, AI/ML, Programming, and more
- Browse and search the knowledge base
- View your conversation analytics
- Customize your experience through Settings
- (If Admin) Manage users, train the chatbot, and view logs

---

## 💻 Installation

### System Requirements

| Component | Minimum |
|-----------|---------|
| Operating System | Windows 10/11, macOS 11+, Ubuntu 20.04+ |
| Java | JDK 17 or higher |
| RAM | 512 MB |
| Storage | 100 MB |
| Display | 1280 × 720 or higher |

### Steps

1. **Install Java 17+**
   - Download from: https://adoptium.net/
   - Verify: `java -version` (should show 17 or higher)

2. **Install Maven 3.9+** (if building from source)
   - Download from: https://maven.apache.org/
   - Verify: `mvn -version`

3. **Build and Run**
   ```bash
   cd NeuraBot-AI/
   mvn clean package
   mvn javafx:run
   ```

4. **OR run the pre-built JAR**
   ```bash
   java -jar neurabot-ai-1.0.0.jar
   ```

---

## 🎬 First Launch

When you first launch NeuraBot AI:

1. **Splash Screen** appears for ~2.5 seconds with an animated neural network visualization
2. **Landing Page** is shown — a premium marketing-style page introducing the application
3. You are prompted to **Login** or **Register**

On first run, the application automatically:
- Creates the data directory at `~/.neurabot/data/`
- Seeds the knowledge base with 30+ articles
- Creates default admin account (`admin` / `admin123`)
- Creates a demo user account (`demo` / `demo123`)

---

## 🔐 Authentication

### Logging In

1. Click **Login** or **Start Chatting** from the Landing Page
2. Enter your **Username** and **Password**
3. Click **Sign In** or press `Enter`

**Demo credentials:**
| Username | Password | Access Level |
|----------|----------|-------------|
| `admin` | `admin123` | Full admin access |
| `demo` | `demo123` | Regular user |

### Creating a New Account

1. Click **Create Account** on the Login screen
2. Fill in all fields:
   - Full Name (e.g., Mohammad Sakib Ahmad)
   - Email Address (valid format required)
   - Username (minimum 3 characters)
   - Password (minimum 6 characters)
   - Confirm Password (must match)
3. Click **Create My Account**
4. On success, you will be redirected to Login

### Password Requirements
- Minimum 6 characters
- Passwords must match exactly
- Email must contain `@` and `.`
- Username must be unique in the system

---

## 🏠 Main Dashboard

After logging in, you arrive at the **Main Dashboard** — your control center.

### Components

**Header Row** — Greeting + current date  
**New Chat Button** — Start a new AI conversation immediately

**Statistics Cards** (5 cards):
- 💬 Total Sessions
- 📨 Messages Sent
- 📚 Knowledge Articles
- 😊 User Satisfaction
- 👥 Registered Users

**AI Status Card** — Shows AI Online status, response speed, and your personal stats

**Recent Conversations** — Last 5 chat sessions with titles and timestamps

**Quick Action Cards** — 4 shortcut tiles for Chat, Knowledge Base, Analytics, Reports

### Navigation Sidebar
| Menu Item | Action |
|-----------|--------|
| 🏠 Dashboard | Return to main dashboard |
| 💬 Chat | Open AI chat interface |
| 📚 Knowledge Base | Browse articles |
| 📊 Analytics | View charts and reports |
| 👑 Admin Panel | (Admins only) User management |
| ⚙️ Settings | Customize the app |
| ℹ️ About | Project information |
| 🌙/☀ Theme | Toggle dark/light mode |
| 🚪 Logout | Sign out |

---

## 💬 Chat Interface

The Chat Interface is where you interact with NeuraBot AI.

### Starting a Conversation

1. Click **💬 Chat** from the sidebar or Dashboard
2. NeuraBot greets you automatically
3. Type your question in the input box at the bottom
4. Press `Enter` or click **Send ↑**

### Chat Features

**Message Bubbles:**
- Your messages appear on the **right** (purple gradient)
- NeuraBot's replies appear on the **left** (dark card)
- Each bubble shows a **timestamp**

**Typing Indicator:**
- When NeuraBot is "thinking", animated bouncing dots appear
- Response delay is realistic (0.6–1.4 seconds)

**Suggestion Chips:**
- Click any suggestion chip at the top of the chat area
- Pre-written questions to get you started quickly

**Session Sidebar:**
- Shows all your past conversations
- Click **+ New Chat** to start fresh
- Past sessions can be viewed (read-only)

### Sample Questions to Ask

**Technology:**
- "What is machine learning?"
- "Explain deep learning"
- "What is artificial intelligence?"
- "Tell me about neural networks"

**Java Programming:**
- "What is Java?"
- "Explain OOP in Java"
- "What are Java Collections?"
- "How do interfaces work in Java?"

**Algorithms:**
- "What is binary search?"
- "Explain sorting algorithms"
- "What is Big O notation?"

**General:**
- "Tell me a joke"
- "What can you do?"
- "Who created you?"
- "How are you?"

### Clearing Chat
Click **🗑 Clear Chat** in the top bar to reset the current conversation.

---

## 📚 Knowledge Base

The Knowledge Base contains 30+ curated articles organized by category.

### Browsing Articles

1. Click **📚 Knowledge Base** from the sidebar
2. Browse articles by clicking category buttons on the left:
   - 📋 All
   - Technology, Artificial Intelligence, Machine Learning
   - Java, Programming, Data Structures
   - Algorithms, Database, Web Development, General

### Searching

Type in the search box to instantly filter articles by keyword.

### Reading an Article

Click on any article card to **expand** it and read the full answer.  
Click again to **collapse** it.

### For Admins: Managing Articles

Click **+ Add FAQ** to add a new article:
- Enter the **Question**
- Enter the **Answer**  
- Set the **Category**
- Add **Keywords** (comma-separated)

Click **✏ Edit** on any card to modify it.  
Click **🗑 Delete** to remove an article (with confirmation).

---

## 📊 Analytics & Reports

The Analytics view shows AI conversation insights.

### Charts

**📈 Conversation Volume** — Bar chart of sessions over the last 7 days  
**😊 Sentiment Distribution** — Pie chart of Positive / Neutral / Negative messages  
**🏆 Top Knowledge Categories** — Ranked progress bars by category usage  
**🔥 Most Asked Questions** — Top 7 frequently asked FAQ entries

### Exporting Reports

Click the export buttons in the left sidebar:

| Button | Output |
|--------|--------|
| 📄 User Report (TXT) | Your personal usage summary |
| 📊 Sessions (CSV) | All your chat sessions in spreadsheet format |
| 🤖 AI Performance | System-wide AI accuracy and response stats |
| 📚 FAQ Export (CSV) | Full knowledge base export |

Reports are saved to: `~/.neurabot/reports/`

---

## ⚙️ Settings

Customize NeuraBot AI to match your preferences.

### Appearance

**Theme:** Toggle between 🌙 Dark Mode and ☀ Light Mode  
**Chat Bubble Style:** Rounded / Flat / Minimal

### AI Personality

Choose how NeuraBot communicates:

| Mode | Style |
|------|-------|
| 😊 Friendly | Warm, casual, emoji-rich |
| 💼 Professional | Formal, no emojis, concise |
| 📖 Teacher | Educational, adds learning tips |
| 💻 Coding Mentor | Code-focused, technical hints |
| 🧠 Expert | Detailed, technical, comprehensive |

### Display

**Font Size:** Slider from 11px to 20px  
**Animation Speed:** Slider from 0.5x (slower) to 2.0x (faster)  
**Show Timestamps:** Toggle timestamps on message bubbles  
**Typing Indicator:** Toggle "NeuraBot is thinking..." animation

### Notifications & Data

**Notifications:** Enable/disable toast popup messages  
**Auto Save:** Automatically save conversations  
**Language:** English / Hindi

### Saving

Click **💾 Save Settings** to apply all changes.  
Click **Reset Defaults** to restore original settings.

---

## 👑 Admin Panel

*Available only to users with Administrator role.*

### User Management

The **Users** tab shows all registered accounts:
- View username, email, role, session count, message count, last login
- Click **👑 Toggle Admin** to promote/demote a selected user
- Click **🗑 Delete User** to remove a user (cannot delete yourself)
- Click **🔄 Refresh** to reload the list

### Chatbot Training

The **Train Chatbot** tab lets you add new knowledge:
1. Enter a **User Question** (what users might ask)
2. Enter the **Bot Answer** (what NeuraBot should reply)
3. Set a **Category** (e.g., Java, AI, Database)
4. Add **Keywords** (comma-separated for better matching)
5. Click **🧠 Train NeuraBot**

Trained entries appear in the "Trained Entries" table at the bottom.

### Activity Logs

The **Activity Logs** tab shows the system audit trail:
- All login/logout events
- Chat session creation
- FAQ updates
- Training activities
- Report exports
- Theme changes

Use the search field to filter logs by user or description.

---

## 🔧 Troubleshooting

### Application Won't Start
- Ensure Java 17+ is installed: `java -version`
- Run from the project root: `mvn javafx:run`
- Check that Maven dependencies downloaded: `mvn clean package`

### Can't Login
- Use demo credentials: `admin` / `admin123`
- Check for extra spaces in username/password
- If you forgot your password, use the "Forgot Password" link (shows demo credentials)

### Chat Not Responding
- Ensure the question ends with a question mark for best matching
- Try simpler keywords: "java" instead of "what is the java programming language"
- Check the Knowledge Base for what topics are covered

### Data Not Saving
- Check write permissions to your home directory (`~/.neurabot/`)
- On Windows: check that no antivirus is blocking the data folder

### Blurry / Small Text (HiDPI)
Add JVM argument when running:
```
-Dglass.win.uiScale=1.5
```

---

## 📂 Data Location

All application data is stored at:

| OS | Path |
|----|------|
| Windows | `C:\Users\YourName\.neurabot\data\` |
| macOS | `/Users/YourName/.neurabot/data/` |
| Linux | `/home/YourName/.neurabot/data/` |

Reports are exported to:
`[home]/.neurabot/reports/`

---

## 📞 Support

For issues or questions about this project:
- **Developer:** Mohammad Sakib Ahmad
- **Project Type:** B.Tech Final Year Project
- **Version:** 1.0

---

*© 2026 NeuraBot AI — Think. Learn. Assist. 🤖*
