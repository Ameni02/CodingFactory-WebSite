@echo off
echo Starting Sentiment Analysis API...

REM Check if Python is installed
python --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Python is not installed or not in PATH. Please install Python and try again.
    exit /b 1
)

REM Check if required packages are installed
echo Checking required packages...
pip install -r requirements.txt

REM Start the Flask API
echo Starting Flask API...
python sentiment_api.py

echo API started successfully!
