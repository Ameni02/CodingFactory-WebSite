import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Deliverable } from '../models/deliverable.model';

@Injectable({
  providedIn: 'root'
})
export class DeliverableService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  constructor(private http: HttpClient) {}

  getDeliverables(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(`${this.apiUrl}/deliverables`);
  }

  getDeliverableById(id: number): Observable<Deliverable> {
    return this.http.get<Deliverable>(`${this.apiUrl}/deliverables/${id}`);
  }

  createDeliverable(
    projectId: number,
    academicSupervisorId: number,
    deliverable: Deliverable,
    descriptionFile: File,
    reportFile: File
  ): Observable<Deliverable> {
    const formData = new FormData();
    formData.append('deliverable', JSON.stringify(deliverable));
    formData.append('descriptionFile', descriptionFile);
    formData.append('reportFile', reportFile);

    return this.http.post<Deliverable>(
      `${this.apiUrl}/projects/${projectId}/supervisors/${academicSupervisorId}/deliverables`,
      formData
    );
  }

  updateDeliverable(id: number, deliverable: Deliverable): Observable<Deliverable> {
    return this.http.put<Deliverable>(`${this.apiUrl}/deliverables/${id}`, deliverable);
  }

  archiveDeliverable(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/deliverables/${id}/archive`, {});
  }

  /**
   * Add a new deliverable with files
   * @param formData FormData containing deliverable data and files
   * @returns Observable with the created deliverable
   */
  addDeliverable(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/deliverables`, formData);
  }
}
