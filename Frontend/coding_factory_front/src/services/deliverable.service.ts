import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Deliverable } from 'src/app/models/deliverable.model';

@Injectable({
  providedIn: 'root',
})
export class DeliverableService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/deliverables';

  constructor(private http: HttpClient) {}

  addDeliverable(formData: FormData): Observable<Deliverable> {
    return this.http.post<Deliverable>(`${this.apiUrl}/add`, formData);
  }
  
  

 

  getDeliverables(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(this.apiUrl);
  }

  getDeliverableById(id: number): Observable<Deliverable> {
    return this.http.get<Deliverable>(`${this.apiUrl}/${id}`);
  }
  

  // Archive a deliverable
  archiveDeliverable(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/archive`, {});
  }
}
