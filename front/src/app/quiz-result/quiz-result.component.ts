import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.css'],
  standalone: false
})
export class QuizResultComponent implements OnInit {
  quizResults: any[] = [];
  myResult: any = null;
  quizId!: number;
  currentUser = 'balkis'; // Replace with dynamic user if needed
  baseUrl = 'http://localhost:8080';
  quizDetails: any = {};

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Get quiz ID from URL
    this.quizId = +this.route.snapshot.paramMap.get('id')!;

    // Fetch quiz details
    this.http.get<any>(`${this.baseUrl}/api/quiz/${this.quizId}/details`).subscribe(details => {
      this.quizDetails = details;
    });

    // Fetch quiz results
    this.http.get<any[]>(`${this.baseUrl}/quiz-results/${this.quizId}`).subscribe(data => {
      this.quizResults = data.sort((a, b) => b.score - a.score);
      this.quizResults.forEach((result, index) => {
        result.rank = index + 1;
      });
      this.myResult = this.quizResults.find(r => r.username === this.currentUser);
    });
  }

  formatTime(time: number): string {
    if (time <= 0) {
      return '0:00';  // Handle case where timeSpent is 0 or negative
    }
  
    const minutes = Math.floor(time / 60);  // Convert seconds to minutes
    const seconds = time % 60;  // Get remaining seconds
    return `${minutes}:${(seconds < 10 ? '0' : '') + seconds}`;  // Format with leading zero for single digits
  }
  

  // Navigate back to quiz list
  goBack() {
    this.router.navigate(['/quizzes']);
  }
}
