FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Create model directory
RUN mkdir -p model

EXPOSE 5001

CMD ["python", "app.py"]
