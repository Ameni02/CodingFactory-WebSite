import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { QuizService } from '../quiz.service';

@Component({
  selector: 'app-quiz-list-without-questions',
  templateUrl: './quiz-list-without-questions.component.html',
  styleUrls: ['./quiz-list-without-questions.component.css'],
  standalone: false
})
export class QuizListWitoutQuestionsComponent implements OnInit, AfterViewInit {
  quizzes: any[] = [];
  dataSource = new MatTableDataSource<any>();
  searchQuery: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private quizService: QuizService) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  ngAfterViewInit(): void {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  loadQuizzes(): void {
    this.quizService.getAllQuizzesWithoutQuestions().subscribe(
      (data) => {
        this.quizzes = data;
        this.dataSource.data = data;
        console.log("Quizzes reçus:", data);
      },
      (error) => {
        console.error("Erreur lors du chargement des quizzes:", error);
      }
    );
  }

  applyFilter(): void {
    this.dataSource.filter = this.searchQuery.trim().toLowerCase();
  }
}
