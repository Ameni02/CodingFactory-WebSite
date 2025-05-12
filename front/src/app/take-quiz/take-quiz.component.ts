import { Component, OnInit, HostListener, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-take-quiz',
  templateUrl: './take-quiz.component.html',
  styleUrls: ['./take-quiz.component.css'],
  standalone: false
})
export class TakeQuizComponent implements OnInit, AfterViewInit {
  quiz: any = null;
  quizId!: number;
  userId: number = 1;
  selectedAnswers: { [index: number]: number } = {};
  timeLeft: number = 180;
  startTime: number = 0;
  timerRunning: boolean = false;
  currentQuestionIndex = 0;
  hasLeft = false;
  baseUrl = 'http://localhost:8080';
  selectedWord = '';
  wordDefinition = '';
  showDefinitionPopup = false;
  popupX = 0;
  popupY = 0;
  hideTimeout: any;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler(event: any) {
    event.returnValue = true;
  }

  @HostListener('document:visibilitychange', [])
  onVisibilityChange() {
    if (document.visibilityState === 'hidden' && !this.hasLeft) {
      this.hasLeft = true;
      this.snackBar.open('⚠️ Leaving the quiz is not allowed.', 'Fermer', {
        duration: 4000,
        panelClass: ['snackbar-error'],
        horizontalPosition: 'center',
        verticalPosition: 'top',
      });
      setTimeout(() => this.router.navigate(['/quizzes']), 1000);
    }
  }

  get currentQuestion() {
    return this.quiz?.questions[this.currentQuestionIndex];
  }

  nextQuestion() {
    if (this.currentQuestionIndex < this.quiz.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  prevQuestion() {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.quizId = +id;
      this.http.get(`http://localhost:8080/api/quiz/${id}`).subscribe((data: any) => {
        this.quiz = data;
        this.startTime = Date.now(); // ⏱️ Démarrage du chrono
        console.log('Quiz loaded:', this.quiz);
      });
    }
  }

  ngAfterViewInit(): void {
    this.timerRunning = true;
    setInterval(() => this.decrementTimeLeft(), 1000);
  }

  decrementTimeLeft(): void {
    if (this.timerRunning) {
      this.timeLeft -= 1;
      if (this.timeLeft <= 0) {
        this.timerRunning = false;
        this.submitQuiz();
      }
    }
  }

  get displayTime(): string {
    const minutes = Math.floor(this.timeLeft / 60);
    const seconds = this.timeLeft % 60;
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  }

  isAnswered(index: number): boolean {
    return this.selectedAnswers[index] !== undefined;
  }

  selectAnswer(index: number, optionId: number): void {
    this.selectedAnswers[index] = optionId;
  }

  submitQuiz(): void {
    if (!this.quiz || !this.quiz.questions) return;

    const answerPayload = this.quiz.questions.map((question: any, index: number) => ({
      questionId: question.id,
      optionId: this.selectedAnswers[index]
    }));

    const timeSpent = Math.floor((Date.now() - this.startTime) / 1000); // en secondes

    console.log("▶️ Payload envoyé :", { answers: answerPayload, timeSpent });

    this.http.post<any>(
      `http://localhost:8080/api/quiz/${this.quizId}/submit/${this.userId}`,
      {
        answers: answerPayload,
        timeSpent: timeSpent
      }
    ).subscribe({
      next: (result) => {
        const total = this.quiz.questions.length;
        this.snackBar.open(`✅ Score : ${result.score} / ${total}`, 'Fermer', {
          duration: 4000,
          panelClass: ['snackbar-success'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
        this.router.navigate(['/quiz-result', this.quizId]);
      },
      error: (err) => {
        console.error('Erreur lors de la soumission du quiz :', err);
        this.snackBar.open('❌ Erreur lors de la soumission.', 'Fermer', {
          duration: 4000,
          panelClass: ['snackbar-error'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }
    });
  }

  @HostListener('mouseup')
  getSelectedWord() {
    const selection = window.getSelection();
    const word = selection?.toString().trim();
    if (word && selection?.rangeCount) {
      const range = selection.getRangeAt(0);
      const rect = range.getBoundingClientRect();
  
      this.popupX = rect.left + window.scrollX;
      this.popupY = rect.top + window.scrollY - 40; // adjust above word
  
      this.selectedWord = word;
  
      this.http.get(`${this.baseUrl}/api/quiz/dictionary?word=${word}`, { responseType: 'text' })
        .subscribe(def => {
          this.wordDefinition = def;
          this.showDefinitionPopup = true;
        }, () => {
          this.wordDefinition = 'Definition not found.';
          this.showDefinitionPopup = true;
        });
  
      // Auto-hide if user moves mouse away after a delay
      clearTimeout(this.hideTimeout);
      this.hideTimeout = setTimeout(() => this.showDefinitionPopup = false, 3000);
    }
  }
  
  keepPopup() {
    clearTimeout(this.hideTimeout);
  }
  
  hidePopup() {
    this.hideTimeout = setTimeout(() => this.showDefinitionPopup = false, 300);
  }
  

}
