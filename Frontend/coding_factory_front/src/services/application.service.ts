import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Application } from '../app/models/application.model';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  constructor(private http: HttpClient, private toastr: ToastrService) {}

  // Add a new application with files
  addApplication(projectId: number, application: Application, cvFile: File, coverLetterFile: File): Observable<Application> {
    const formData = new FormData();
    
    // Create a clean application object without the project details (just ID)
    const appData = {
      studentName: application.studentName,
      studentEmail: application.studentEmail,
      status: application.status,
      archived: application.archived
    };

    // Append the application as JSON
    formData.append('application', new Blob([JSON.stringify(appData)], {
      type: 'application/json'
    }));

    // Append files
    formData.append('cvFile', cvFile);
    formData.append('coverLetterFile', coverLetterFile);

    return this.http.post<Application>(
      `${this.apiUrl}/projects/${projectId}/applications`, 
      formData
    ).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  // Get application by ID
  getApplication(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.apiUrl}/applications/${id}`).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  // Download a file (CV or cover letter)
  downloadFile(applicationId: number, fileType: 'cv' | 'coverLetter'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/applications/${applicationId}/download/${fileType}`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  // Accept an application
  acceptApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/accept`, {}).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  // Reject an application
  rejectApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/reject`, {}).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  // Improved error handling
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = error.error?.message || error.message || 'Server error';
      
      // Special handling for 400 errors (validation errors)
      if (error.status === 400 && error.error.errors) {
        errorMessage = 'Validation errors: ' + 
          Object.values(error.error.errors).join(', ');
      }
    }

    this.toastr.error(errorMessage, 'Error');
    console.error('API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}