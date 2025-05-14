import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TestService {
  private apiUrl = 'http://localhost:8080/pfespace/api/test';

  constructor(private http: HttpClient) {}

  /**
   * Echo back the request data
   * @param data The data to echo
   * @returns Observable with the echoed data
   */
  echo(data: any): Observable<any> {
    console.log('Sending echo request:', data);
    return this.http.post(`${this.apiUrl}/echo`, data)
      .pipe(
        catchError(error => {
          console.error('Error in echo request:', error);
          throw error;
        })
      );
  }

  /**
   * Ping the server
   * @returns Observable with the ping response
   */
  ping(): Observable<any> {
    return this.http.get(`${this.apiUrl}/ping`)
      .pipe(
        catchError(error => {
          console.error('Error in ping request:', error);
          throw error;
        })
      );
  }
}
