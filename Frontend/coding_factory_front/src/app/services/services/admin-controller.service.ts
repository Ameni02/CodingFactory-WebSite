import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class AdminControllerService {
  private apiUrl = 'http://localhost:8081/api/v1/admin';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if (!token) {
      console.error('❌ No token found.');
      throw new Error('No authentication token found.');
    }
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/list_user`, { headers: this.getHeaders() }).pipe(
      catchError((error) => {
        console.error('🚨 Error fetching users:', error);
        return throwError(() => error);
      })
    );
  }

  banUser(userId: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/ban/${userId}`, {}, { headers: this.getHeaders(), responseType: 'text' }).pipe(
      catchError((error) => {
        console.error('🚨 Error banning user:', error);
        return throwError(() => error);
      })
    );
  }

  unbanUser(userId: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/unban/${userId}`, {}, { headers: this.getHeaders(), responseType: 'text' }).pipe(
      catchError((error) => {
        console.error('🚨 Error unbanning user:', error);
        return throwError(() => error);
      })
    );
  }

  deleteUser(userId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/delete/${userId}`, { headers: this.getHeaders(), responseType: 'text' }).pipe(
      catchError((error) => {
        console.error('🚨 Error deleting user:', error);
        return throwError(() => error);
      })
    );
  }
}
