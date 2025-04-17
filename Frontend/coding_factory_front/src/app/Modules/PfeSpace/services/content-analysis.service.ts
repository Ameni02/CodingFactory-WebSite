import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ContentAnalysisResult } from '../models/content-analysis-result.model';

@Injectable({
  providedIn: 'root'
})
export class ContentAnalysisService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/content-analysis';

  constructor(private http: HttpClient) {}

  /**
   * Analyze a report file for plagiarism and AI-generated content
   * @param reportFile The report file to analyze
   * @returns Observable with analysis results
   */
  analyzeReport(reportFile: File): Observable<ContentAnalysisResult> {
    const formData = new FormData();
    formData.append('reportFile', reportFile);

    return this.http.post<ContentAnalysisResult>(`${this.apiUrl}/analyze-report`, formData)
      .pipe(
        catchError(error => {
          console.error('Error analyzing report:', error);
          throw error;
        })
      );
  }

  /**
   * Analyze a report for an existing deliverable
   * @param deliverableId The ID of the deliverable
   * @returns Observable with analysis results
   */
  analyzeDeliverableReport(deliverableId: number): Observable<ContentAnalysisResult> {
    return this.http.post<ContentAnalysisResult>(`${this.apiUrl}/deliverables/${deliverableId}/analyze`, {})
      .pipe(
        catchError(error => {
          console.error('Error analyzing deliverable report:', error);
          throw error;
        })
      );
  }
}