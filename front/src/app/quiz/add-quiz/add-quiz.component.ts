import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-quiz',
  templateUrl: './add-quiz.component.html',
  styleUrls: ['./add-quiz.component.css'],
  standalone: false
})
export class AddQuizComponent {
  topic: string = '';
  numQuestions: number = 5;
  loading: boolean = false; // ✅ Doit être défini
  errorMessage: string = '';
  rawAiText: string = '';
  aiQuiz: any = null;

  title: string = '';
  description: string = '';
  category: string ='';

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {}

  generateQuiz(): void {
    if (!this.topic.trim() || this.numQuestions < 1) {
      this.errorMessage = '❗ Veuillez saisir un sujet valide et au moins une question.';
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
        this.errorMessage = '❌ Erreur lors de la génération du quiz.';
        this.loading = false;
      }
    });
  }

  addQuiz(): void {
    if (!this.aiQuiz?.quiz) {
      this.snackBar.open('❌ Aucun quiz généré à ajouter !', 'Fermer', { duration: 3000 });
      return;
    }

    const formattedQuestions = this.aiQuiz.quiz.map((q: any) => ({
      question: q.question,
      answer: q.answer,
      options: Array.isArray(q.options)
        ? q.options.map((opt: string) => ({ value: opt, isCorrect: opt === q.answer }))
        : []
    }));

    const payload = {
      title: this.title || 'Quiz IA',
      category: this.getCategoryFromConfig(),
      description: this.description || 'Généré automatiquement',
      questions: formattedQuestions
    };

    console.log("📤 Payload :", JSON.stringify(payload, null, 2));

    this.http.post('http://localhost:8080/api/quiz/questions', payload).subscribe({
      next: () => {
        this.snackBar.open('✅ Quiz ajouté avec succès !', 'Fermer', { duration: 3000 });
        this.topic = '';
        this.numQuestions = 5;
        this.title = '';
        this.description = '';
        this.aiQuiz = null;
        this.rawAiText = '';
        this.router.navigate(['/quizzes']);
      },
      error: (err) => {
        console.error('❌ Erreur lors de l\'ajout :', err);
        this.snackBar.open('❌ Erreur lors de l\'ajout du quiz.', 'Fermer', { duration: 3000 });
      }
    });
  }
  getCategoryFromConfig() {
    return this.aiQuiz?.category || 'Général';
  }
  
}
