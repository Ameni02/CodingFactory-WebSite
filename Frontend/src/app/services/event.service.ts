import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable, tap} from "rxjs";
import {environment} from "../../environments/environment";

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
  private eventsSubject = new BehaviorSubject<any[]>([]);
  events$ = this.eventsSubject.asObservable();

  constructor(private http:HttpClient) { }

  getActiveEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${environment. apiUrl}events/active`).pipe(
      tap(events => this.eventsSubject.next(events))
    )  }
  saveEvent(eventRequest: EventRequest): Observable<any> {
    return this.http.post<any>(`${environment. apiUrl}events`,eventRequest);
  }


  uploadEventPicture(eventId: number, file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.http.post(`${environment. apiUrl}events/upload/${eventId}`, formData);
  }
  refreshEvents() {
    this.getActiveEvents().subscribe();
  }
  deleteEvent(eventId: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}events/${eventId}`);
  }
  getEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}events/active`);
  }

  updateEvent(event: any): Observable<any> {
    return this.http.put(`${environment.apiUrl}events/${event.id}`, event);
  }
}
