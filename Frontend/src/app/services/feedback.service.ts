import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  private apiUrl = `${environment.apiUrl}feedback`;

  constructor(private http: HttpClient) {}


  submitFeedback(feedbackRequest:any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, feedbackRequest);
  }


  getFeedbackForEvent(eventId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/event/${eventId}`);
  }
  deleteFeedback(feedbackId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${feedbackId}`);
  }
}
