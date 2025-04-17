import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Formation {
  id: number;
  titre: string;
  pdfFileName: string;
  archived: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private apiUrl = 'http://localhost:8057/api/formations'; // modifie l'URL si besoin

  constructor(private http: HttpClient) {}

  // ✅ 1. Récupérer toutes les formations
  getAllFormations(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}`);
  }

  // ✅ 2. Récupérer uniquement les formations non archivées
  getAllFormationsNonArchivees(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees`);
  }

  // ✅ 3. Récupérer une formation par ID
  getFormationById(id: number): Observable<Formation> {
    return this.http.get<Formation>(`${this.apiUrl}/${id}`);
  }

  // ✅ 4. Upload d’un fichier PDF + titre
  uploadPdf(titre: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('titre', titre);
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-pdf`, formData, { responseType: 'text' });
  }

  // ✅ 5. Télécharger le PDF d’une formation
  getPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: 'blob'
    });
  }

  // ✅ 6. Archiver une formation
  archiveFormation(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/archive/${id}`, null, { responseType: 'text' });
  }

  // ✅ 7. Désarchiver une formation
  unarchiveFormation(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/unarchive/${id}`, null, { responseType: 'text' });
  }
}