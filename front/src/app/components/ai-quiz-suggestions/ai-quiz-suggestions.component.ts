import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-ai-quiz-suggestions',
  templateUrl: './ai-quiz-suggestions.component.html',
  styleUrls: ['./ai-quiz-suggestions.component.css'],
  standalone: false 
})
export class AiQuizSuggestionsComponent {
  topic: string = '';
  numQuestions: number = 5;
  rawAiText: string = '';
  errorMessage: string = '';
  loading: boolean = false;
  aiQuiz: any = null;

  @Output() importRequested = new EventEmitter<any>(); // ✅ va émettre un QuizDto

  constructor(private http: HttpClient) {}

  generateQuiz() {
    if (!this.topic.trim() || this.numQuestions < 1) {
      this.errorMessage = '❌ Saisis un sujet et au moins une question.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.rawAiText = '';
    this.aiQuiz = null;

    this.http.post('http://localhost:8080/generate', {
      topic: this.topic,
      numQuestions: this.numQuestions
    }, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        this.rawAiText = response;
        try {
          this.aiQuiz = JSON.parse(response);
        } catch (e) {
          this.errorMessage = '❌ Erreur de lecture du JSON généré.';
        }
        this.loading = false;
      },
      error: () => {
        this.errorMessage = '❌ Erreur lors de la génération.';
        this.loading = false;
      }
    });
  }

  // ✅ Convertit le JSON IA vers un QuizDto attendu par le backend
  convertAIQuizToQuizDto(aiResponse: any): any {
    return {
      title: "Quiz généré par IA",
      description: "Quiz automatiquement généré",
      isVerified: false,
      questions: aiResponse.quiz.map((q: any) => ({
        question: q.question,
        type: "text",
        options: q.options.map((opt: any) => ({
          text: opt.value,
          isCorrect: true 
          
        }))
      }))
    };
    
  }

  // ✅ Envoie le quiz converti au parent (ex: AddQuizComponent)
  importQuiz() {
    if (this.aiQuiz?.quiz) {
      const quizDto = this.convertAIQuizToQuizDto(this.aiQuiz);
      this.importRequested.emit(quizDto);
    } else {
      this.errorMessage = '❌ Aucune question à importer.';
    }
  }
}
