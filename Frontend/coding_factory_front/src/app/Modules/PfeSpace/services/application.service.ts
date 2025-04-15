import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from '../models/application.model';

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

  getApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.apiUrl}/applications`);
  }

  updateApplication(id: number, application: Application): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}`, application);
  }

  acceptApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/accept`, {});
  }

  rejectApplication(id: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/applications/${id}/reject`, {});
  }

  archiveApplication(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/applications/${id}/archive`, {});
  }

  /**
   * Download a file (CV or cover letter) from an application
   * @param id The application ID
   * @param fileType The type of file to download ('cv' or 'coverLetter')
   * @returns Observable with the file blob
   */
  downloadFile(id: number, fileType: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/applications/${id}/files/${fileType}`, {
      responseType: 'blob'
    });
  }
}
