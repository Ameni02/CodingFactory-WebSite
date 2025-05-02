import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Formation } from '../models/formation.model';

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private apiUrl = 'http://localhost:8057/api/formations';

  constructor(private http: HttpClient) { }

  // Get all formations
  getAllFormations(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}`);
  }

  // Get all non-archived formations
  getAllFormationsNonArchivees(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees`);
  }

  // Get all formations sorted by sentiment score
  getAllFormationsBySentiment(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/by-sentiment`);
  }

  // Get all non-archived formations sorted by sentiment score
  getAllNonArchivedFormationsBySentiment(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees/by-sentiment`);
  }

  // Get all formations sorted by positive comment ratio
  getAllFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/by-positive-ratio`);
  }

  // Get all non-archived formations sorted by positive comment ratio
  getAllNonArchivedFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees/by-positive-ratio`);
  }

  // Get a formation by ID
  getFormationById(id: number): Observable<Formation> {
    return this.http.get<Formation>(`${this.apiUrl}/${id}`);
  }

  // Upload a PDF file with a title
  uploadPdf(titre: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('titre', titre);
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-pdf`, formData, { responseType: 'text' });
  }

  // Get the PDF file for a formation
  getPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: 'blob'
    });
  }

  // Archive a formation
  archiveFormation(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/archive/${id}`, null, { responseType: 'text' });
  }

  // Unarchive a formation
  unarchiveFormation(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/unarchive/${id}`, null, { responseType: 'text' });
  }
}
