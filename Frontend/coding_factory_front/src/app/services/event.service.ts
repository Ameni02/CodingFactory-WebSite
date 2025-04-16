import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, catchError, tap, throwError } from "rxjs";
import { environment } from 'src/environments/environment';

export interface EventRequest {
  title: string;
  description: string;
  date: string;
  location: string;
  maxParticipants: number;
  price: number;
  category: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = environment.apiUrl + 'events';
  private eventsSubject = new BehaviorSubject<any[]>([]);
  events$ = this.eventsSubject.asObservable();

  constructor(private http: HttpClient) { }

  getActiveEvents(): Observable<any[]> {
    console.log('Fetching active events from:', `${this.apiUrl}/active`);
    return this.http.get<any[]>(`${this.apiUrl}/active`).pipe(
      tap(events => {
        console.log('Received events:', events);
        this.eventsSubject.next(events);
      }),
      catchError(error => {
        console.error('Error fetching events:', error);
        return throwError(() => new Error(`Failed to fetch events: ${error.message}`));
      })
    );
  }

  saveEvent(eventRequest: EventRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, eventRequest);
  }

  uploadEventPicture(eventId: number, file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/upload/${eventId}`, formData);
  }

  refreshEvents() {
    this.getActiveEvents().subscribe();
  }

  deleteEvent(eventId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${eventId}`);
  }

  getEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/active`);
  }

  updateEvent(event: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${event.id}`, event);
  }

  // Debug methods
  getEventCounts(): Observable<any> {
    return this.http.get(`${environment.apiUrl}debug/events/count`).pipe(
      catchError(error => {
        console.error('Error fetching event counts:', error);
        return throwError(() => new Error(`Failed to fetch event counts: ${error.message}`));
      })
    );
  }

  checkEventImages(): Observable<any> {
    return this.http.get(`${environment.apiUrl}debug/events/images`).pipe(
      catchError(error => {
        console.error('Error checking event images:', error);
        return throwError(() => new Error(`Failed to check event images: ${error.message}`));
      })
    );
  }

  getEventWithImage(id: number): Observable<any> {
    return this.http.get(`${environment.apiUrl}debug/events/${id}/image`).pipe(
      catchError(error => {
        console.error(`Error fetching event ${id} with image:`, error);
        return throwError(() => new Error(`Failed to fetch event with image: ${error.message}`));
      })
    );
  }
}
