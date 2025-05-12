import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {UserPredictionRequest} from "../../models/user-prediction-request";
import {User} from "../../models/user";


@Injectable({ providedIn: 'root' })
export class AdminControllerService {
  private apiUrl = 'http://localhost:8081/api/v1/admin';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No authentication token found.');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  evaluateUser(userId: number, data: UserPredictionRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/evaluate/${userId}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError((error) => {
        console.error('🚨 Error evaluating user:', error);
        return throwError(() => error);
      })
    );
  }
}
