# NeuraBot AI — UML Diagrams
## Software Architecture Documentation

---

## 1. CLASS DIAGRAM (Core Architecture)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         NeuraBot AI – Class Diagram                         │
└─────────────────────────────────────────────────────────────────────────────┘

 ┌──────────────┐     ┌──────────────────┐     ┌───────────────────┐
 │   User       │     │  ChatSession     │     │    Message        │
 ├──────────────┤     ├──────────────────┤     ├───────────────────┤
 │-id: String   │1  * │-id: String       │1  * │-id: String        │
 │-fullName     │─────│-userId: String   │─────│-sessionId: String │
 │-email        │     │-title: String    │     │-content: String   │
 │-username     │     │-startTime: LDT   │     │-sender: Sender    │
 │-passwordHash │     │-messages: List   │     │-timestamp: LDT    │
 │-role: String │     │-messageCount     │     │-sentiment: Sentiment│
 │-totalSessions│     ├──────────────────┤     │-detectedIntent    │
 │-aiPersonality│     │+addMessage()     │     ├───────────────────┤
 ├──────────────┤     │+endSession()     │     │+isFromUser()      │
 │+isAdmin()    │     │+getFormattedTime()│     │+getFormattedTime()│
 │+getFormatted │     └──────────────────┘     └───────────────────┘
 └──────────────┘

 ┌────────────────────────────────────────┐
 │             ChatbotEngine              │
 ├────────────────────────────────────────┤
 │-nlpProcessor: NLPProcessor             │
 │-knowledgeBase: KnowledgeBase           │
 │-intentDetector: IntentDetector         │
 │-sentimentAnalyzer: SentimentAnalyzer   │
 │-recommendEngine: RecommendationEngine  │
 │-executor: ExecutorService              │
 ├────────────────────────────────────────┤
 │+processMessage(input, session): Message│
 │+getTypingDelay(): long                 │
 │-applyPersonality(response): String     │
 │-generateResponse(result): String       │
 └────────────────────────────────────────┘
          │                    │
          ▼                    ▼
 ┌─────────────────┐   ┌──────────────────┐
 │  NLPProcessor   │   │  KnowledgeBase   │
 ├─────────────────┤   ├──────────────────┤
 │-stopWords: Set  │   │-faqs: List<FAQ>  │
 │-keywords: Set   │   │-instance (singleton)│
 ├─────────────────┤   ├──────────────────┤
 │+tokenize()      │   │+findBestMatch()  │
 │+extractKeywords()│  │+searchFAQs()     │
 │+isQuestion()    │   │+addFAQ()         │
 └─────────────────┘   │+updateFAQ()      │
                       │+deleteFAQ()      │
          │            └──────────────────┘
          ▼
 ┌─────────────────────┐   ┌───────────────────┐
 │  IntentDetector     │   │ SentimentAnalyzer │
 ├─────────────────────┤   ├───────────────────┤
 │-patterns: Map<>     │   │-positiveWords: Set│
 ├─────────────────────┤   │-negativeWords: Set│
 │+detect(tokens)      │   │-intensifiers: Set │
 │  :IntentType        │   ├───────────────────┤
 └─────────────────────┘   │+analyze(): Sentiment│
                           │+analyzeBatch()    │
                           └───────────────────┘

 ┌──────────────────────────────────────────┐
 │           DatabaseManager (Singleton)    │
 ├──────────────────────────────────────────┤
 │-instance: DatabaseManager               │
 │-users: Map<String, User>                │
 │-sessions: Map<String, ChatSession>      │
 │-activityLogs: List<ActivityLog>         │
 │-userSettings: Map<String, UserSettings> │
 │-gson: Gson                              │
 ├──────────────────────────────────────────┤
 │+getInstance(): DatabaseManager          │
 │+initialize()                            │
 │+registerUser(User): boolean             │
 │+authenticate(username, pw): User        │
 │+saveSession(ChatSession)                │
 │+logActivity(userId, type, desc)         │
 │+getUserSettings(userId): UserSettings   │
 │+getAllUsers(): List<User>               │
 └──────────────────────────────────────────┘

 ┌────────────────────────────────┐
 │           FAQ                  │
 ├────────────────────────────────┤
 │-id: String                     │
 │-question: String               │
 │-answer: String                 │
 │-category: String               │
 │-keywords: String[]             │
 │-hitCount: int                  │
 │-isActive: boolean              │
 ├────────────────────────────────┤
 │+incrementHitCount()            │
 └────────────────────────────────┘

 ┌──────────────────────────────────────┐
 │          AnalyticsManager            │
 ├──────────────────────────────────────┤
 │-db: DatabaseManager                  │
 │-kb: KnowledgeBase                    │
 │-sentimentAnalyzer: SentimentAnalyzer │
 ├──────────────────────────────────────┤
 │+getTotalUsers(): long                │
 │+getTopTopics(n): List<Entry<>>       │
 │+getSentimentDistribution(): Map<>    │
 │+getActivityByDay(days): Map<>        │
 │+getUserSatisfactionRate(): double    │
 └──────────────────────────────────────┘

 ┌──────────────────────────────┐
 │       ThemeManager           │
 ├──────────────────────────────┤
 │-currentTheme: Theme          │
 │-registeredScenes: List<Scene>│
 ├──────────────────────────────┤
 │+registerScene(Scene)         │
 │+setTheme(Theme)              │
 │+toggleTheme()                │
 │+getThemeIcon(): String       │
 └──────────────────────────────┘
```

---

## 2. USE CASE DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────┐
│                    NeuraBot AI – Use Case Diagram                   │
└─────────────────────────────────────────────────────────────────────┘

    ┌──────────────────────────────────────────────────────────────┐
    │                     NeuraBot AI System                       │
    │                                                              │
    │  ┌────────────────────────────────────────────────────────┐  │
    │  │  Authentication                                        │  │
    │  │  ○ Register Account                                    │  │
    │  │  ○ Login                                               │  │
    │  │  ○ Logout                                              │  │
    │  └────────────────────────────────────────────────────────┘  │
    │                                                              │
    │  ┌────────────────────────────────────────────────────────┐  │
    │  │  Chat                                                  │  │
    │  │  ○ Send Message                                        │  │
    │  │  ○ Receive AI Response                                 │  │
    │  │  ○ View Conversation History                           │  │
    │  │  ○ Start New Chat                                      │  │
    │  │  ○ Clear Chat                                          │  │
    │  └────────────────────────────────────────────────────────┘  │
    │                                                              │
    │  ┌────────────────────────────────────────────────────────┐  │
    │  │  Knowledge Base                                        │  │
    │  │  ○ Browse Articles                                     │  │
    │  │  ○ Search Knowledge                                    │  │
    │  │  ○ View by Category                                    │  │
    │  └────────────────────────────────────────────────────────┘  │
    │                                                              │
    │  ┌────────────────────────────────────────────────────────┐  │
    │  │  Settings & Reports                                    │  │
    │  │  ○ Change Theme                                        │  │
    │  │  ○ Set AI Personality                                  │  │
    │  │  ○ Export Reports (TXT/CSV)                            │  │
    │  │  ○ View Analytics                                      │  │
    │  └────────────────────────────────────────────────────────┘  │
    │                                                              │
    │  ┌────────────────────────────────────────────────────────┐  │
    │  │  Admin Only                                            │  │
    │  │  ○ Manage Users (promote/delete)                       │  │
    │  │  ○ Train Chatbot (add FAQ)                             │  │
    │  │  ○ View Activity Logs                                  │  │
    │  │  ○ Delete Any User                                     │  │
    │  └────────────────────────────────────────────────────────┘  │
    └──────────────────────────────────────────────────────────────┘

    [User] ─────────────────────────────────────────────────────▶ All UC except Admin
    [Admin] ────────────────────────────────────────────────────▶ All UC including Admin
    [System/Timer] ─────────────────────────────────────────────▶ Auto-save, Seed data
```

---

## 3. SEQUENCE DIAGRAM — Chat Message Flow

```
User          ChatInterface      ChatbotEngine       NLPProcessor
 │                  │                  │                   │
 │──sendMessage()──▶│                  │                   │
 │                  │──processMsg()───▶│                   │
 │                  │                  │──tokenize()───────▶│
 │                  │                  │◀──tokens──────────│
 │                  │                  │──extractKeywords()▶│
 │                  │                  │◀──keywords────────│
 │                  │                  │                   │
 │                  │              IntentDetector      KnowledgeBase
 │                  │                  │──detect()────────▶│
 │                  │                  │◀──IntentType──────│
 │                  │                  │                   │
 │                  │                  │──findBestMatch()──────────▶│
 │                  │                  │◀──FAQ or null─────────────│
 │                  │                  │                   │
 │                  │                  │──generateResponse()        
 │                  │                  │──applyPersonality()        
 │                  │◀──Message────────│                   │
 │                  │                  │                   │
 │                  │──displayMessage()│                   │
 │◀─────bubbleAnim──│                  │                   │
 │                  │                  │                   │
 │                  │──saveSession()──▶DatabaseManager     │
 │                  │◀──saved──────────│                   │
```

---

## 4. SEQUENCE DIAGRAM — User Authentication

```
User          LoginScreen       DatabaseManager        SplashScreen
 │                  │                  │                   │
 │──enterCreds()───▶│                  │                   │
 │                  │──authenticate()─▶│                   │
 │                  │                  │──hashPassword()    │
 │                  │                  │──findUser()        │
 │                  │◀──User/null──────│                   │
 │                  │                  │                   │
 │  [if User!=null] │                  │                   │
 │                  │──updateLastLogin()▶│                  │
 │                  │──logActivity()───▶│                   │
 │                  │──showDashboard()──────────────────────▶│
 │◀────Dashboard─────────────────────────────────────────────│
 │                  │                  │                   │
 │  [if User==null] │                  │                   │
 │                  │──shakeAnimation() │                   │
 │◀──"Invalid creds"│                  │                   │
```

---

## 5. COMPONENT DIAGRAM

```
┌────────────────────────────────────────────────────────────┐
│                      NeuraBot AI Application               │
│                                                            │
│  ┌──────────────┐    ┌──────────────┐   ┌──────────────┐  │
│  │   View Layer │    │  AI Engine   │   │  Data Layer  │  │
│  │              │    │              │   │              │  │
│  │ SplashScreen │───▶│ChatbotEngine │───▶DatabaseManager│  │
│  │ LandingPage  │    │              │   │              │  │
│  │ LoginScreen  │    │NLPProcessor  │   │ KnowledgeBase│  │
│  │ MainDashboard│    │IntentDetect  │   │              │  │
│  │ ChatInterface│───▶│SentimentAnz  │   │ FileManager  │  │
│  │ KBView       │    │RecommEngine  │   │              │  │
│  │ AnalyticsView│───▶│              │   │ UserSettings │  │
│  │ AdminPanel   │    └──────────────┘   └──────────────┘  │
│  │ SettingsView │                                          │
│  │ AboutView    │    ┌──────────────┐   ┌──────────────┐  │
│  └──────────────┘    │  Analytics   │   │  Reports     │  │
│                      │              │   │              │  │
│                      │AnalyticsMgr  │   │ReportGenerat │  │
│                      │              │   │              │  │
│                      └──────────────┘   └──────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                    Utility Layer                     │  │
│  │  ThemeManager    NotificationManager    FileManager  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  Data Storage                        │  │
│  │  ~/.neurabot/data/users.json                         │  │
│  │  ~/.neurabot/data/sessions.json                      │  │
│  │  ~/.neurabot/data/knowledge_base.json                │  │
│  │  ~/.neurabot/data/activity_logs.json                 │  │
│  │  ~/.neurabot/data/settings.json                      │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

---

## 6. ENTITY-RELATIONSHIP DIAGRAM (Data Model)

```
┌──────────┐        ┌─────────────┐        ┌─────────────┐
│   User   │1──────*│ ChatSession │1──────*│   Message   │
├──────────┤        ├─────────────┤        ├─────────────┤
│id (PK)   │        │id (PK)      │        │id (PK)      │
│fullName  │        │userId (FK)  │        │sessionId(FK)│
│email     │        │title        │        │content      │
│username  │        │startTime    │        │sender       │
│pwHash    │        │messageCount │        │timestamp    │
│role      │        │dominantTopic│        │sentiment    │
│createdAt │        │isActive     │        │intent       │
└──────────┘        └─────────────┘        └─────────────┘

┌──────────┐        ┌─────────────┐        ┌─────────────┐
│   User   │1──────1│UserSettings │  ┌────*│ ActivityLog │
├──────────┤        ├─────────────┤  │     ├─────────────┤
│id (PK)   │        │userId (PK/FK)│  │     │id (PK)      │
└──────────┘        │theme        │  │     │userId (FK)  │
                    │personality  │  │     │username     │
                    │fontSize     │  │     │logType      │
                    │animSpeed    │  │     │description  │
                    │language     │  │     │timestamp    │
                    └─────────────┘  │     └─────────────┘
                                     │
┌─────────┐                         │
│  User   │1────────────────────────┘
└─────────┘

┌────────────────────┐
│        FAQ         │
├────────────────────┤
│id (PK)             │
│question            │
│answer              │
│category            │
│keywords[]          │
│hitCount            │
│confidenceScore     │
│isActive            │
│isLearned           │
│createdAt           │
│updatedAt           │
└────────────────────┘
```

---

*© 2026 NeuraBot AI — Mohammad Sakib Ahmad*
