@echo off
title Time Tracker Application
echo Starting Time Tracker ...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java to run this application
    pause
    exit /b 1
)

java -jar "E:\Programming\TimeTracker\build\libs\TimeTracker-1.0.jar"

REM Check if the application ran successfully
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start
    echo Make sure TimeTracker-1.0.jar is in the same folder as this script
)

echo.
echo Application closed.
pause

