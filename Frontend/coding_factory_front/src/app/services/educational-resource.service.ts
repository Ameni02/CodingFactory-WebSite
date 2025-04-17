import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Formation } from './formation.service';

export interface RessourcePedagogique {
  id?: number;
  titre: string;
  description: string;
  cheminFichier?: string;
  formation: Formation;
}



@Injectable({
  providedIn: 'root'
})
export class EducationalResourceService {

  private apiUrl = 'http://localhost:8080/api/ressources'; // Ajuste l'URL selon ton backend

  constructor(private http: HttpClient) {}

  // 🔹 Récupérer toutes les ressources
  getAllRessources(): Observable<RessourcePedagogique[]> {
    return this.http.get<RessourcePedagogique[]>(this.apiUrl);
  }

  // 🔹 Récupérer une ressource par ID
  getRessourceById(id: number): Observable<RessourcePedagogique> {
    return this.http.get<RessourcePedagogique>(`${this.apiUrl}/${id}`);
  }

    // ✅ Nouvelle méthode pour le téléchargement
    downloadRessourceById(id: number): Observable<Blob> {
      return this.http.get(`${this.apiUrl}/${id}/download`, {
        responseType: 'blob'
      });
    }

  // 🔹 Upload (ajouter une nouvelle ressource)
  uploadRessource(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/upload`, formData);
  }
}