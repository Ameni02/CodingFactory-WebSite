import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface DashboardStats {
  users: {
    total: number;
    new: number;
    active: number;
  };
  trainings: {
    total: number;
    active: number;
    draft: number;
  };
  evaluations: {
    total: number;
    pending: number;
    completed: number;
  };
  consulting: {
    total: number;
    ongoing: number;
    completed: number;
  };
  pfespace: {
    projects: number;
    applications: number;
    submissions: number;
    supervisors: number;
  };
}

export interface Activity {
  id: number;
  type: string;
  description: string;
  timestamp: Date;
  user?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private baseUrl = 'http://localhost:8080/api/admin'; // Base URL for the admin API

  constructor(private http: HttpClient) { }

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.baseUrl}/dashboard/stats`).pipe(
      catchError(this.handleError<DashboardStats>('getDashboardStats'))
    );
  }

  getRecentActivities(): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.baseUrl}/dashboard/activities`).pipe(
      catchError(this.handleError<Activity[]>('getRecentActivities'))
    );
  }

  // Error handling
  private handleError<T>(operation = 'operation') {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      console.error('Error details:', error);

      // Return the error to be handled by the component
      return throwError(() => error);
    };
  }
}
