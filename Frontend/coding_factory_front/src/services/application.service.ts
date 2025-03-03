import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from 'src/app/models/application.model'; 

@Injectable({
  providedIn: 'root',
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/applications';

  constructor(private http: HttpClient) {}

  getApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(this.apiUrl);
  }

  getApplicationById(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.apiUrl}/${id}`);
  }

  acceptApplication(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/accept`, {});
  }

  addApplication(application: Application): Observable<Application> {
    return this.http.post<Application>(this.apiUrl, application);
  }
  rejectApplication(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/reject`, {});
  }
}