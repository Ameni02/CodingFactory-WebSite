import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Consultant } from '../models/Consulting';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ConsultantService {

  private apiUrl = 'http://localhost:8080/pfespace/api/consultants'; // Adjust base URL as needed

  constructor(private http: HttpClient) {}

  // 🔹 Create Consultant
  createConsultant(consultant: Consultant): Observable<Consultant> {
    return this.http.post<Consultant>(this.apiUrl, consultant);
  }

  // 🔹 Get All Consultants
  getAllConsultants(): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(this.apiUrl);
  }

  // 🔹 Get Consultant by ID
  getConsultantById(id: number): Observable<Consultant> {
    return this.http.get<Consultant>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Update Consultant
  updateConsultant(id: number, consultant: Consultant): Observable<Consultant> {
    return this.http.put<Consultant>(`${this.apiUrl}/${id}`, consultant);
  }

  // 🔹 Delete Consultant
  deleteConsultant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  /* checkEmailExists(email: string) {
    return this.http.get<boolean>(`${this.apiUrl}/email-exists`, {
      params: { email }
    });
  } */
    checkEmailExists(email: string, excludeId?: number) {
      let params: any = { email };
      if (excludeId) {
        params.excludeId = excludeId;
      }
    
      return this.http.get<boolean>(`${this.apiUrl}/email-exists`, { params });
    }
}
