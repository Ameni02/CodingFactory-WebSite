import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ParicipationService {
  private apiUrl = `${environment.apiUrl}participations`;
  private participationSubject = new BehaviorSubject<boolean>(false);
  public participation$ = this.participationSubject.asObservable();

  constructor(private http:HttpClient) { }
  registerParticipation(participationRequest: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, participationRequest).pipe(
      tap(()=> this.participationSubject.next(true))
    );
  }


  getParticipantsByEvent(eventId: number): Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/event/${eventId}`);
  }
  isUserParticipating(eventId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/is-participating/${eventId}/${userId}`);
  }

  deleteParticipation(participationId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${participationId}`);
  }
}
