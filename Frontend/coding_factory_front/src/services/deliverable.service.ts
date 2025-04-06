import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    return this.http.post<Deliverable>(this.DELIVERABLES_API, formData);
  }

  getAllDeliverables(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(this.DELIVERABLES_API);
  }

  getDeliverableById(id: number): Observable<Deliverable> {
    return this.http.get<Deliverable>(`${this.DELIVERABLES_API}/${id}`);
  }

  getDeliverablesWithoutProject(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(`${this.DELIVERABLES_API}/without-project`);
  }

  archiveDeliverable(id: number): Observable<void> {
    return this.http.put<void>(`${this.DELIVERABLES_API}/${id}/archive`, {});
  }

  downloadPlagiarismReport(id: number): Observable<Blob> {
    return this.http.get(`${this.DELIVERABLES_API}/reports/${id}`, {
      responseType: 'blob'
    });
  }

  // PfeRestController endpoints
  submitDeliverableAlternative(formData: FormData): Observable<Deliverable> {
    return this.http.post<Deliverable>(`${this.PFE_API}/deliverables/add`, formData);
  }

  // PlagiarismController endpoints
  checkPlagiarism(file: File, strictness: number = 0.7): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('strictness', strictness.toString());
    return this.http.post(`${this.PLAGIARISM_API}`, formData);
  }
}