import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Deliverable } from 'src/app/models/deliverable.model'; 
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class DeliverableService {
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe/deliverables';

  constructor(private http: HttpClient) {}

  getDeliverables(): Observable<Deliverable[]> {
    return this.http.get<Deliverable[]>(this.apiUrl).pipe(
      tap((data) => console.log('Fetched deliverables:', data)) // Log the response
    );
  }
  addDeliverable(deliverable: Deliverable): Observable<Deliverable> {
    return this.http.post<Deliverable>(this.apiUrl, deliverable);
  }

  getDeliverableById(id: number): Observable<Deliverable> {
    return this.http.get<Deliverable>(`${this.apiUrl}/${id}`);
  }
  archiveDeliverable(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/archive`, {});
  }
}

