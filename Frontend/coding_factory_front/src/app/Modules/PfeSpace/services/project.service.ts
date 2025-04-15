import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Project } from '../models/project.model';

@Injectable({
  providedIn: 'root'
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

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/projects/${id}`);
  }

  createProject(project: Project, file: File): Observable<Project> {
    const formData = new FormData();
    formData.append('project', JSON.stringify(project));
    formData.append('file', file);

    return this.http.post<Project>(`${this.apiUrl}/projects`, formData);
  }

  /**
   * Add a new project (alias for createProject for compatibility)
   * @param project The project data
   * @param file The project description file
   * @returns Observable with the created project
   */
  addProject(project: any, file: File): Observable<Project> {
    return this.createProject(project, file);
  }

  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/projects/${id}`, project);
  }

  archiveProject(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/projects/${id}/archive`, {});
  }

  // Statistics methods
  getProjectStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats/projects`);
  }

  getApplicationStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats/applications`);
  }

  getDeliverableStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats/deliverables`);
  }

  getRecentApplications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recent/applications`);
  }

  getRecentEvaluations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recent/evaluations`);
  }

  getRecentProjects(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recent/projects`);
  }

  /**
   * Download a file from a project
   * @param id The project ID
   * @param fileType The type of file to download (usually 'description')
   * @returns Observable with the file blob
   */
  downloadFile(id: number, fileType: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/projects/${id}/files/${fileType}`, {
      responseType: 'blob'
    });
  }
}
