import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/projects';

  constructor(private http: HttpClient) {}

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  addProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project);
  }

  // Method to archive a project
  archiveProject(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/archive`, {});
  }

  uploadFile(file: File): Observable<{ message: string, filePath: string }> {
    const formData = new FormData();
    formData.append('file', file); // Key must match @RequestParam("file")
    return this.http.post<{ message: string, filePath: string }>(`${this.apiUrl}/upload`, formData);
  }
  downloadFile(filePath: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download?filePath=${encodeURIComponent(filePath)}`, {
      responseType: 'blob' // Ensure the response is treated as a binary file
    });
  }
  

}