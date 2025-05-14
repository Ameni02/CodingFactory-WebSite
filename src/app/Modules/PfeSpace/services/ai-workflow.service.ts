import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CoverLetterRequest, CoverLetterResponse, MatchProjectsResponse } from '../models/ai-workflow.model';
import { PfeSpaceConfig } from '../config/pfe-space.config';

@Injectable({
  providedIn: 'root'
})
export class AiWorkflowService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/ai-workflow';

  constructor(private http: HttpClient) {}

  /**
   * Match CV to projects using SBERT
   * @param cvFile The CV file (PDF)
   * @returns Observable with matched projects and scores
   */
  matchProjects(cvFile: File): Observable<MatchProjectsResponse> {
    const formData = new FormData();
    formData.append('cvFile', cvFile);

    return this.http.post<MatchProjectsResponse>(`${this.apiUrl}/match-projects`, formData)
    .pipe(
      catchError(error => {
        console.error('Error matching projects:', error);
        throw error;
      })
    );
  }

  /**
   * Generate a cover letter for a specific project
   * @param projectId The ID of the project
   * @param cvText The extracted text from the CV
   * @param studentName The name of the student
   * @param studentEmail The email of the student
   * @returns Observable with generated cover letter
   */
  generateCoverLetter(
    projectId: number,
    cvText: string,
    studentName: string,
    studentEmail: string
  ): Observable<CoverLetterResponse> {
    const requestBody: CoverLetterRequest = {
      projectId,
      cvText,
      studentName,
      studentEmail
    };

    console.log('Sending cover letter request:', JSON.stringify(requestBody));

    return this.http.post<CoverLetterResponse>(`${this.apiUrl}/generate-cover-letter`, requestBody)
    .pipe(
      catchError(error => {
        console.error('Error generating cover letter:', error);
        throw error;
      })
    );
  }
}
