@echo off
title NeuraBot AI - Intelligent Conversational Assistant
echo.
echo  =========================================
echo   NeuraBot AI - Starting Application...
echo  =========================================
echo.

:: Set Java 25 as JAVA_HOME (Oracle JDK 25)
set JAVA_HOME=C:\Program Files\Java\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%

:: Maven path
set MVN=C:\Users\Ghosty\Desktop\2025\Java\VoiceLink\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd

:: Navigate to project root
cd /d "%~dp0"

:: Run the JavaFX application
echo Launching NeuraBot AI...
"%MVN%" javafx:run

pause
