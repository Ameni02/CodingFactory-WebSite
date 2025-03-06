import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from 'src/app/models/project.model';

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
    return this.http.post<Project>(`${this.apiUrl}/add`, formData);
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
      responseType: 'blob', // Ensure the response is treated as a binary file
    });
  }
}
