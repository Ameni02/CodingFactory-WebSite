import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-mlguesser',
  templateUrl: './mlguesser.component.html',
  styleUrls: ['./mlguesser.component.css'],
  standalone: false
})
export class MlguesserComponent {
  userInput: string = '';
  predictionResult: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient) {}
  getWorstQuizTitlesAndPredict() {
    this.http.get<{ worst_titles: string[] }>('http://localhost:8080/api/quiz/quizresult/worst-third')
      .subscribe({
        next: (response) => {
          const titles = response.worst_titles;
          if (titles.length === 0) {
            this.errorMessage = 'No poor quizzes found.';
            return;
          }
  
          // 👉 Utilise chaque titre pour faire une prédiction
          for (let title of titles) {
            const payload = { text: title };
            this.http.post<{ predicted_category: string }>('http://localhost:8080/predict', payload)
              .subscribe({
                next: (res) => {
                  this.predictionResult = res.predicted_category;
                  console.log(`Prediction for "${title}": ${res.predicted_category}`);
                },
                error: (err) => {
                  console.error(`❌ Error predicting for "${title}"`, err);
                }
              });
          }
        },
        error: (err) => {
          console.error('❌ Failed to get worst quiz titles', err);
          this.errorMessage = 'Unable to retrieve quiz data.';
        }
      });
  }
  sendPrediction() {
    this.errorMessage = '';
    this.predictionResult = '';

    const payload = { text: this. userInput };

    this.http.post<{ predicted_category: string }>('http://localhost:8080/predict', payload)
      .subscribe({
        next: (response) => {
          this.predictionResult = response.predicted_category;
        },
        error: (error) => {
          console.error('Error:', error);
          this.errorMessage = 'Failed to get prediction.';
        }
      });
  }
}
