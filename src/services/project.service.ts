import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Project } from 'src/app/models/project.model';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    })
  };

  constructor(private http: HttpClient) {}
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/projects/active`, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      })
    }).pipe(
      catchError(error => {
        console.error('API Error:', error);
        return throwError(() => new Error(
          error.error?.message || 
          'Failed to load projects. Please try again later.'
        ));
      })
    );
  }


  getActiveProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/projects/active`).pipe(
      catchError(this.handleError)
    );
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/projects/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  addProject(project: Project, file: File): Observable<Project> {
    const formData = new FormData();
    
    // Append each project property individually as form data
    formData.append('title', project.title);
    formData.append('field', project.field);
    formData.append('requiredSkills', project.requiredSkills);
    formData.append('startDate', project.startDate.toString()); // Ensure startDate is in a proper format
    formData.append('endDate', project.endDate.toString());     // Ensure endDate is in a proper format
    formData.append('numberOfPositions', project.numberOfPositions.toString());
    formData.append('companyName', project.companyName);
    formData.append('professionalSupervisor', project.professionalSupervisor);
    formData.append('companyAddress', project.companyAddress);
    formData.append('companyEmail', project.companyEmail);
    formData.append('companyPhone', project.companyPhone);
    
    // Append the file
    formData.append('file', file);
  
    // Send the POST request with FormData
    return this.http.post<Project>(`${this.apiUrl}/projects/add`, formData).pipe(
      catchError(this.handleError)
    );
  }
  

  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/projects/${id}`, project).pipe(
      catchError(this.handleError)
    );
  }

  // Method to archive a project
  archiveProject(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/projects/${id}/archive`, {}).pipe(
      catchError(this.handleError)
    );
  }

  uploadFile(file: File): Observable<{ message: string, filePath: string }> {
    const formData = new FormData();
    formData.append('file', file); // Key must match @RequestParam("file")
    return this.http.post<{ message: string, filePath: string }>(`${this.apiUrl}/projects/upload`, formData).pipe(
      catchError(this.handleError)
    );
  }

  downloadFile(projectId: number, fileType: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/download/${fileType}`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error && typeof error.error === 'string') {
        try {
          const errorObj = JSON.parse(error.error);
          errorMessage = errorObj.message || errorMessage;
        } catch (e) {
          // If parsing fails, use the original error message
        }
      }
    }
    console.error('Error details:', error);
    return throwError(() => new Error(errorMessage));
  }
}
