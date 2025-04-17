import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private baseUrl = 'http://localhost:8080/notifications'; // 🔁 adapte si besoin

  constructor(private http: HttpClient) {}

  sendEmail(to: string, subject: string, message: string): Observable<string> {
    const params = new HttpParams()
      .set('to', to)
      .set('subject', subject)
      .set('message', message);

    return this.http.post(this.baseUrl + '/email', null, { params, responseType: 'text' });
  }
}
