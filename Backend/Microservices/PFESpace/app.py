from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer

app = Flask(__name__)

# Load your model once globally
model = SentenceTransformer('all-MiniLM-L6-v2')

@app.route('/')
def home():
    return "Flask embedding service is running."

@app.route('/get-embeddings', methods=['POST'])
def get_embeddings():
    data = request.get_json()
    texts = data.get("texts", [])
    embeddings = model.encode(texts).tolist()
    return jsonify({"embeddings": embeddings})

@app.route('/get-embedding', methods=['POST'])
def get_embedding():
    data = request.get_json()
    text = data.get("text", "")
    embedding = model.encode([text]).tolist()[0]
    return jsonify({"embedding": embedding})

if __name__ == '__main__':
    app.run(debug=True)