import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from '../models/application.model';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });
    }
    return new HttpHeaders();
  }

  addApplication(projectId: number, application: any, cvFile: File, coverLetterFile: File): Observable<Application> {
    const formData = new FormData();
    formData.append('application', JSON.stringify(application));
    formData.append('cvFile', cvFile);
    formData.append('coverLetterFile', coverLetterFile);

    return this.http.post<Application>(
      `${this.apiUrl}/projects/${projectId}/applications`,
      formData,
      { headers: this.getAuthHeaders() }
    );
  }

  getApplication(id: number): Observable<Application> {
    return this.http.get<Application>(
      `${this.apiUrl}/applications/${id}`,
      { headers: this.getAuthHeaders() }
    );
  }

  getApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(
      `${this.apiUrl}/applications`,
      { headers: this.getAuthHeaders() }
    );
  }

  updateApplication(id: number, application: Application): Observable<Application> {
    return this.http.put<Application>(
      `${this.apiUrl}/applications/${id}`,
      application,
      { headers: this.getAuthHeaders() }
    );
  }

  acceptApplication(id: number): Observable<Application> {
    return this.http.put<Application>(
      `${this.apiUrl}/applications/${id}/accept`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  rejectApplication(id: number): Observable<Application> {
    return this.http.put<Application>(
      `${this.apiUrl}/applications/${id}/reject`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  archiveApplication(id: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/applications/${id}/archive`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Download a file (CV or cover letter) from an application
   * @param id The application ID
   * @param fileType The type of file to download ('cv' or 'coverLetter')
   * @returns Observable with the file blob
   */
  downloadFile(id: number, fileType: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/applications/${id}/files/${fileType}`, {
      responseType: 'blob',
      headers: this.getAuthHeaders()
    });
  }
}
