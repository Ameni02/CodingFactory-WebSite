import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicSupervisor } from '../models/academicsupervisor.model';
import { PfeSpaceConfig } from '../config/pfe-space.config';

@Injectable({
  providedIn: 'root'
})
export class AcademicSupervisorService {

  private baseUrl: string = 'http://localhost:8080/pfespace/api/pfe/academic-supervisors'; // Replace with your Spring Boot API URL

  constructor(private http: HttpClient) { }

  // Get all academic supervisors
  getAllSupervisors(): Observable<AcademicSupervisor[]> {
    return this.http.get<AcademicSupervisor[]>(`${this.baseUrl}`);
  }

  // Get a specific academic supervisor by id
  getSupervisorById(id: number): Observable<AcademicSupervisor> {
    return this.http.get<AcademicSupervisor>(`${this.baseUrl}/${id}`);
  }

  // Create a new academic supervisor
  createSupervisor(supervisor: AcademicSupervisor): Observable<AcademicSupervisor> {
    return this.http.post<AcademicSupervisor>(this.baseUrl, supervisor);
  }

  // Update an existing academic supervisor
  updateSupervisor(id: number, supervisor: AcademicSupervisor): Observable<AcademicSupervisor> {
    return this.http.put<AcademicSupervisor>(`${this.baseUrl}/${id}`, supervisor);
  }

  // Delete an academic supervisor by id
  deleteSupervisor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
