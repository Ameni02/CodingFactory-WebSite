import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuizService } from '../quiz/quiz.service';

@Component({
  selector: 'app-quiz-list',
  templateUrl: './quiz-list.component.html',
  standalone:false,
  styleUrls: ['./quiz-list.component.css']
})
export class QuizListComponent implements OnInit {
  quizzes: any[] = [];
  filteredQuizzes: any[] = [];
  searchQuery: string = '';

  constructor(private quizService: QuizService, private router: Router) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.quizService.getAllQuizzesWithoutQuestions().subscribe(
      (data) => {
        this.quizzes = data;
        this.filteredQuizzes = data; // Initialize filtered list
        console.log('Available quizzes:', this.quizzes);
      },
      (error) => {
        console.error('Error loading quizzes:', error);
      }
    );
  }

  filterQuizzes(): void {
    const query = this.searchQuery.toLowerCase();
    this.filteredQuizzes = this.quizzes.filter(quiz =>
      quiz.title.toLowerCase().includes(query) || quiz.description.toLowerCase().includes(query)
    );
  }

  takeQuiz(quizId: number): void {
    this.router.navigate(['/take-quiz', quizId]);
  }
}
