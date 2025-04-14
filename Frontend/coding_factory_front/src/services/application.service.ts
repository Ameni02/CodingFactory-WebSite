import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from 'src/app/models/application.model';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  constructor(private http: HttpClient) {}

  addApplication(projectId: number, application: any, cvFile: File, coverLetterFile: File): Observable<Application> {
    const formData = new FormData();
    formData.append('application', JSON.stringify(application));
    formData.append('cvFile', cvFile);
    formData.append('coverLetterFile', coverLetterFile);
    
    return this.http.post<Application>(
      `${this.apiUrl}/projects/${projectId}/applications`, 
      formData
    );
  }

  getApplication(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.apiUrl}/applications/${id}`);
  }

  downloadFile(applicationId: number, fileType: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/applications/${applicationId}/files/${fileType}`, {
      responseType: 'blob'
    });
  }

  acceptApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/accept`, {});
  }

  rejectApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/reject`, {});
  }
}