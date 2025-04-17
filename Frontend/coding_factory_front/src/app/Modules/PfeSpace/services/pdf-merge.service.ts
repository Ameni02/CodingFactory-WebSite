import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PfeSpaceConfig } from '../config/pfe-space.config';

@Injectable({
  providedIn: 'root'
})
export class PdfMergeService {

  private apiUrl = 'http://localhost:8080/pfespace/pdf/merge';  // Adjust this URL to your Spring Boot server

  constructor(private http: HttpClient) { }

  mergePdf(files: FormData): Observable<Blob> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'application/pdf');
    return this.http.post(this.apiUrl, files, {
      headers,
      responseType: 'blob'
    });
  }
}
