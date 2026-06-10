# NeuraBot AI — Installation Guide
## Step-by-Step Setup for Windows, macOS, and Linux

---

## ✅ Prerequisites Checklist

Before installing, ensure you have the following:

| Requirement | Version | How to Check |
|-------------|---------|--------------|
| Java JDK | 17 or higher | `java -version` |
| Apache Maven | 3.8+ | `mvn -version` |
| Git (optional) | Any | `git --version` |
| RAM | Minimum 512 MB | System settings |
| Disk Space | Minimum 200 MB | Drive properties |
| Screen Resolution | 1280×720 or higher | Display settings |

---

## 🪟 Windows Installation

### Step 1: Install Java 17+

1. Download **Eclipse Temurin JDK 21 LTS** from:
   https://adoptium.net/temurin/releases/?version=21

2. Run the `.msi` installer with default settings
3. Verify installation:
   ```powershell
   java -version
   # Should show: java version "21.x.x" or higher
   ```

### Step 2: Install Apache Maven

**Option A: Manual Install**
1. Download Maven 3.9 from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to system PATH:
   - Right-click `This PC` → Properties → Advanced → Environment Variables
   - Add `MAVEN_HOME = C:\Program Files\Apache\maven`
   - Add `%MAVEN_HOME%\bin` to the `Path` variable
4. Verify: `mvn -version`

**Option B: Using Chocolatey (faster)**
```powershell
# Install Chocolatey if not present
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven -y
```

### Step 3: Build the Project

```powershell
# Navigate to project directory
cd "C:\path\to\NeuraBot-AI"

# Clean and build
mvn clean package -DskipTests

# Run the application
mvn javafx:run
```

### Step 4: Create Desktop Shortcut (Optional)

After building, create a `run.bat` file in the project root:
```batch
@echo off
title NeuraBot AI
cd /d "%~dp0"
mvn javafx:run
pause
```

---

## 🍎 macOS Installation

### Step 1: Install Java

Using Homebrew (recommended):
```bash
# Install Homebrew if not present
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 21
brew install openjdk@21

# Add to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

### Step 2: Install Maven

```bash
brew install maven
mvn -version
```

### Step 3: Build and Run

```bash
cd ~/path/to/NeuraBot-AI
mvn clean package -DskipTests
mvn javafx:run
```

---

## 🐧 Linux (Ubuntu/Debian) Installation

### Step 1: Install Java

```bash
sudo apt update
sudo apt install openjdk-21-jdk -y
java -version
```

### Step 2: Install Maven

```bash
sudo apt install maven -y
mvn -version
```

### Step 3: Build and Run

```bash
cd ~/NeuraBot-AI
mvn clean package -DskipTests
mvn javafx:run
```

---

## 🔨 Build Commands Reference

| Command | Description |
|---------|-------------|
| `mvn clean` | Remove previous build artifacts |
| `mvn compile` | Compile Java source code only |
| `mvn package` | Build the JAR file |
| `mvn javafx:run` | Run the JavaFX application |
| `mvn clean package` | Clean + build (recommended) |
| `mvn clean package -DskipTests` | Build without running tests |

---

## 🏃 Running the Application

### Method 1: Maven Plugin (Recommended for Development)
```bash
mvn javafx:run
```

### Method 2: Run the Executable JAR
```bash
# From the project root directory
java --module-path "$JAVAFX_HOME/lib" \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/neurabot-ai-1.0.0-shaded.jar
```

### Method 3: IDE (IntelliJ IDEA / Eclipse)

**IntelliJ IDEA:**
1. File → Open → Select project folder
2. Wait for Maven to import dependencies
3. Right-click `App.java` → Run `App.main()`
4. If JavaFX issues: Add `-Djavafx.verbose=true` to VM options

**Eclipse:**
1. File → Import → Maven → Existing Maven Projects
2. Browse to project folder → Finish
3. Right-click `App.java` → Run As → Java Application

---

## 📁 Data Directory

The application automatically creates its data directory on first launch:

| Platform | Path |
|----------|------|
| Windows | `C:\Users\YourName\.neurabot\` |
| macOS | `/Users/YourName/.neurabot/` |
| Linux | `/home/YourName/.neurabot/` |

**Contents:**
```
~/.neurabot/
├── data/
│   ├── users.json          # User accounts
│   ├── sessions.json       # Chat history
│   ├── knowledge_base.json # FAQ database
│   ├── activity_logs.json  # Audit log
│   └── settings.json       # User preferences
└── reports/                # Generated reports (TXT, CSV)
```

**To reset all data** (fresh start):
```bash
# Windows
Remove-Item -Recurse -Force "$env:USERPROFILE\.neurabot"

# macOS/Linux
rm -rf ~/.neurabot
```

---

## 🔐 Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Full Administrator |
| `demo` | `demo123` | Regular User |

> **Security note:** Change the admin password after first login for any production deployment.

---

## ⚠️ Common Issues & Fixes

### Issue: `mvn` command not found
```
Solution: Add Maven's bin directory to your system PATH
Windows: Control Panel > System > Environment Variables
```

### Issue: `JavaFX runtime components are missing`
```
Solution: The pom.xml includes JavaFX dependencies via Maven.
Ensure you're running with: mvn javafx:run (NOT java -jar directly)
```

### Issue: `Module java.xml not found` (Java 9+ modules)
```
Add to pom.xml <jvmArguments>:
--add-opens java.base/java.lang=ALL-UNNAMED
```

### Issue: Application window is too small / HiDPI blur
```
Add JVM argument:
-Dglass.win.uiScale=1.5  (Windows)
-Dglass.gtk.uiScale=1.5  (Linux)
```

### Issue: `SHA-256 MessageDigest not found`
```
Ensure Java security policies are not restricted.
Oracle JDK users: check jre/lib/security/java.security
```

### Issue: `Failed to create ~/.neurabot directory`
```
Check write permissions:
Windows: Run IDE/terminal as Administrator
Linux/Mac: chmod 755 ~/
```

### Issue: `NullPointerException` on startup
```
Delete the data directory and restart for a fresh initialization:
Windows: Remove-Item -Recurse "$env:USERPROFILE\.neurabot"
```

---

## 🧪 Quick Verification Test

After installation, verify everything works:

1. ✅ Application launches with animated splash screen
2. ✅ Landing page shows with particle animation
3. ✅ Login with `admin` / `admin123` succeeds
4. ✅ Dashboard shows statistics cards
5. ✅ Chat interface responds to "What is AI?"
6. ✅ Knowledge Base shows 30+ articles
7. ✅ Analytics shows charts
8. ✅ Admin panel loads (users + training tabs)
9. ✅ Settings save and persist
10. ✅ Dark/Light theme toggle works

---

## 📞 Support

For installation issues:
- **Developer:** Mohammad Sakib Ahmad
- **Java Version Tested:** Java 17, 21, 25
- **Platform Tested:** Windows 10/11, Ubuntu 22.04

---

*© 2026 NeuraBot AI — Think. Learn. Assist. 🤖*
