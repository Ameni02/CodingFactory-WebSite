#!/bin/bash
echo "Starting Sentiment Analysis API..."

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Python is not installed. Please install Python and try again."
    exit 1
fi

# Check if required packages are installed
echo "Checking required packages..."
pip3 install -r requirements.txt

# Start the Flask API
echo "Starting Flask API..."
python3 sentiment_api.py

echo "API started successfully!"
