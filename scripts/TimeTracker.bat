chcp 65001
@echo off
title Time Tracker Application

REM Change to project root directory to find .env file
cd /d "E:\Programming\TimeTracker"

REM Load environment variables from .env file
if exist .env (
    for /f "usebackq eol=# tokens=1,2 delims==" %%i in (".env") do (
        set "%%i=%%j"
    )
)

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java to run this application
    pause
    exit /b 1
)

java -jar "build\libs\TimeTracker-1.0.jar"

REM Check if the application ran successfully
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start
    echo Make sure TimeTracker-1.0.jar exists and HOURLY_GBP_RATE is set in .env file
)

echo.
echo Application closed.
pause