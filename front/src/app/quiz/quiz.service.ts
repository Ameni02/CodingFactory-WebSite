import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quiz'; // Base URL de l'API

  constructor(private http: HttpClient) {
  }

  // Ajouter un quiz avec des questions et des options
  addQuiz(quiz: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/questions`, quiz).pipe(
      tap((response) => console.log('Quiz ajouté avec succès:', response)),
      catchError((error) => {
        console.error('Erreur lors de l\'ajout du quiz:', error);
        return of(null);
      })
    );
  }

  // Récupérer tous les quiz sans questions
  getAllQuizzesWithoutQuestions(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/all`).pipe(
      tap((data) => console.log('Quizzes reçus:', data)),
      catchError((error) => {
        console.error('Erreur lors de la récupération des quizzes:', error);
        return of([]); // Retourner un tableau vide en cas d'erreur
      })
    );
  }

  // Service Angular
  deleteQuiz(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => console.log(`Quiz supprimé avec ID: ${id}`)),
      catchError((error) => {
        console.error('Erreur lors de la suppression du quiz:', error);
        return of(null); // Gérer l'erreur proprement
      })
    );
  }

  getQuizById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`).pipe(
      tap((data) => console.log(`Quiz récupéré avec ID ${id}:`, data)),
      catchError((error) => {
        console.error('Erreur lors de la récupération du quiz:', error);
        return of(null); // Gérer l'erreur proprement
      })
    );
  }

// Récupérer un quiz avec ses questions et options
  getQuizWithDetails(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}/details`).pipe(
      tap((data) => console.log(`Quiz récupéré avec ID ${id}:`, data)),
      catchError((error) => {
        console.error('Erreur lors de la récupération du quiz:', error);
        return of(null);
      })
    );
  }

// Method to update a quiz by ID
  updateQuiz(quizId: number, quizDto: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${quizId}`, quizDto).pipe(
      tap((response) => console.log('Quiz mis à jour avec succès:', response)),
      catchError((error) => {
        console.error('Erreur lors de la mise à jour du quiz:', error);
        return of(null);
      })
    );
  }

  submitQuiz(quizId: number, userId: number, answers: any[]): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${quizId}/submit/${userId}`, answers);
  }

  getQuizForUser(quizId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${quizId}/user-details`).pipe(
      tap((data) => console.log(`✅ Quiz loaded for user with ID ${quizId}:`, data)),
      catchError((error) => {
        console.error('❌ Error fetching user quiz details:', error);
        return of(null);
      })
    );
  }


}
