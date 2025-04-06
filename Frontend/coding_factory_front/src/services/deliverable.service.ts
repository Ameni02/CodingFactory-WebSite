import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Deliverable } from 'src/app/models/deliverable.model';

@Injectable({
  providedIn: 'root'
})
export class DeliverableService {
  private readonly BASE_URL = 'http://localhost:8080/pfespace';

  // Main deliverables endpoints (DeliverableController)
  private get DELIVERABLES_API() {
    return `${this.BASE_URL}/api/deliverables`;
  }

  // PFE endpoints (PfeRestController)
  private get PFE_API() {
    return `${this.BASE_URL}/api/pfe`;
  }

  // Plagiarism endpoints (PlagiarismController)
  private get PLAGIARISM_API() {
    return `${this.BASE_URL}/api/plagiarism`;
  }

  constructor(private http: HttpClient) {}

  // DeliverableController endpoints
  submitDeliverable(formData: FormData): Observable<Deliverable> {
    console.log('Submitting deliverable to:', this.DELIVERABLES_API);
    return this.http.post<Deliverable>(this.DELIVERABLES_API, formData).pipe(
      tap(response => console.log('Deliverable submission response:', response)),
      catchError(this.handleError)
    );
  }

  getAllDeliverables(): Observable<Deliverable[]> {
    console.log('Fetching all deliverables from:', this.DELIVERABLES_API);
    return this.http.get<Deliverable[]>(this.DELIVERABLES_API).pipe(
      tap(response => console.log('Deliverables response:', response)),
      catchError(this.handleError)
    );
  }

  getDeliverableById(id: number): Observable<Deliverable> {
    return this.http.get<Deliverable>(`${this.DELIVERABLES_API}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getDeliverablesWithoutProject(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(`${this.DELIVERABLES_API}/without-project`).pipe(
      catchError(this.handleError)
    );
  }

  archiveDeliverable(id: number): Observable<void> {
    return this.http.put<void>(`${this.DELIVERABLES_API}/${id}/archive`, {}).pipe(
      catchError(this.handleError)
    );
  }

  downloadPlagiarismReport(id: number): Observable<Blob> {
    return this.http.get(`${this.DELIVERABLES_API}/reports/${id}`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  // PfeRestController endpoints
  submitDeliverableAlternative(formData: FormData): Observable<Deliverable> {
    console.log('Submitting deliverable alternative to:', `${this.PFE_API}/deliverables/add`);
    return this.http.post<Deliverable>(`${this.PFE_API}/deliverables/add`, formData).pipe(
      tap(response => console.log('Alternative submission response:', response)),
      catchError(this.handleError)
    );
  }

  // PlagiarismController endpoints
  checkPlagiarism(file: File, strictness: number = 0.7): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('strictness', strictness.toString());
    return this.http.post(`${this.PLAGIARISM_API}`, formData).pipe(
      tap(response => console.log('Plagiarism check response:', response)),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Error occurred:', error);
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error) {
        if (typeof error.error === 'string') {
          errorMessage = error.error;
        } else if (error.error.message) {
          errorMessage = error.error.message;
        }
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}