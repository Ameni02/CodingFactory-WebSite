import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-stats',
  templateUrl: './user-stats.component.html',
  styleUrls: ['./user-stats.component.css'],
  standalone: false
})
export class UserStatsComponent implements OnInit {
  currentUser = 1;  // Replace with dynamic username if needed
  baseUrl = 'http://localhost:8080';
  quizzesTaken: any[] = [];
  averageTimeSpent: number = 0;
  averageCorrectAnswers: number = 0;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // Fetching quizzes and stats for the user
    this.http.get<any[]>(`${this.baseUrl}/quiz-results/user/${this.currentUser}`).subscribe(data => {
      this.quizzesTaken = data;
      this.calculateStats();
    });
  }

  // Calculate aggregated stats
  calculateStats() {
    const totalTime = this.quizzesTaken.reduce((sum, quiz) => sum + quiz.timeSpent, 0);
    const totalCorrectAnswers = this.quizzesTaken.reduce((sum, quiz) => sum + quiz.correctAnswers, 0);
    const totalQuizzes = this.quizzesTaken.length;

    this.averageTimeSpent = totalTime / totalQuizzes;
    this.averageCorrectAnswers = (totalCorrectAnswers / (totalQuizzes * 100)) * 100;
  }

  // Format time from seconds to mm:ss
  formatTime(time: number): string {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  }

  goBack() {
    this.router.navigate(['/quizzes']);
  }

 // Method to download certificate
 downloadCertificate() {
  // Construct the URL to request the certificate (replace with actual backend endpoint)
  const certificateUrl = `${this.baseUrl}/api/quiz/generate-certificate?name=UserName&courseName=QuizCourse`; // Modify params as needed

  // Make an HTTP request to get the PDF file
  this.http.get(certificateUrl, { responseType: 'blob' }).subscribe((pdfBlob) => {
    // Create an object URL for the PDF
    const fileURL = URL.createObjectURL(pdfBlob);

    // Create a link element and trigger a download
    const link = document.createElement('a');
    link.href = fileURL;
    link.download = 'certificate.pdf'; // Filename for the downloaded certificate
    link.click();
  });
}

}
