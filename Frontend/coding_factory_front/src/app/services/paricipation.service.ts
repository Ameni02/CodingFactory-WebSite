import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, catchError, tap, throwError } from "rxjs";
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ParicipationService {
  private apiUrl = environment.apiUrl + 'participations';
  private participationSubject = new BehaviorSubject<boolean>(false);
  public participation$ = this.participationSubject.asObservable();

  constructor(private http: HttpClient) { }

  registerParticipation(participationRequest: any): Observable<any> {
    console.log('Registering participation with request:', participationRequest);
    console.log('API URL:', this.apiUrl);

    // Ensure we're sending the correct format expected by the backend
    const payload = {
      eventId: participationRequest.eventId,
      userId: participationRequest.userId
    };

    return this.http.post<any>(`${this.apiUrl}`, payload).pipe(
      tap((response) => {
        console.log('Participation registration successful:', response);
        this.participationSubject.next(true);
      }),
      catchError(error => {
        console.error('Error registering participation:', error);
        return throwError(() => error);
      })
    );
  }

  getParticipantsByEvent(eventId: number): Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/event/${eventId}`).pipe(
      catchError(error => {
        console.error('Error getting participants by event:', error);
        return throwError(() => error);
      })
    );
  }

  isUserParticipating(eventId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/is-participating/${eventId}/${userId}`).pipe(
      catchError(error => {
        console.error('Error checking if user is participating:', error);
        return throwError(() => error);
      })
    );
  }

  deleteParticipation(participationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${participationId}`).pipe(
      catchError(error => {
        console.error('Error deleting participation:', error);
        return throwError(() => error);
      })
    );
  }
}
