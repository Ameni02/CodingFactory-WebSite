import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PfeSpaceConfig } from '../config/pfe-space.config';

@Injectable({
  providedIn: 'root'
})
export class CvAnalysisService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  constructor(private http: HttpClient) {}

  analyzeCv(projectId: number, cvFile: File): Observable<{ isAdaptable: boolean, score: number, feedback: string }> {
    const formData = new FormData();
    formData.append('cvFile', cvFile);

    return this.http.post<{ isAdaptable: boolean, score: number, feedback: string }>(
      `${this.apiUrl}/projects/${projectId}/analyze-cv`,
      formData
    ).pipe(
      catchError(error => {
        console.error('Error analyzing CV:', error);
        throw error;
      })
    );
  }
}
