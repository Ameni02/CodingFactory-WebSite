import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, map } from 'rxjs';

export interface Comment {
  id?: number;
  content: string;
  rating: number;
  createdAt?: Date;
  sentimentLabel?: string;
  sentimentScore?: number;
  formation: {
    id: number;
  };
  userName: string;
  category?: string;
}

export interface Formation {
  id: number;
  titre: string;
  pdfFileName: string;
  archived: boolean;
  comments?: Comment[];
  averageSentimentScore?: number;
  positiveCommentRatio?: number;
  totalCommentCount?: number;
  positiveCommentCount?: number;
  neutralCommentCount?: number;
  negativeCommentCount?: number;
  dominantSentiment?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private apiUrl = 'http://localhost:8057/api/formations'; // Direct URL
  private commentApiUrl = 'http://localhost:8057/api/comments';

  constructor(private http: HttpClient) {}

  // ✅ 1. Récupérer toutes les formations
  getAllFormations(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}`).pipe(
      catchError(error => {
        console.error('Error fetching all formations:', error);
        return of([]);
      })
    );
  }

  // ✅ 2. Récupérer uniquement les formations non archivées
  getAllFormationsNonArchivees(): Observable<Formation[]> {
    console.log('Fetching non-archived formations from URL:', `${this.apiUrl}/non-archivees`);

    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees`).pipe(
      catchError(error => {
        console.error('Error fetching non-archived formations:', error);
        return of([]);
      })
    );
  }

  // ✅ 3. Récupérer une formation par ID
  getFormationById(id: number): Observable<Formation> {
    return this.http.get<Formation>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching formation with ID ${id}:`, error);
        throw error;
      })
    );
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

  // 8. Récupérer les formations triées par score de sentiment (du plus élevé au plus bas)
  getAllFormationsBySentiment(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/by-sentiment`).pipe(
      catchError(error => {
        console.error('Error fetching formations by sentiment:', error);
        return of([]);
      })
    );
  }

  // 9. Récupérer les formations non archivées triées par score de sentiment
  getAllNonArchivedFormationsBySentiment(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees/by-sentiment`).pipe(
      catchError(error => {
        console.error('Error fetching non-archived formations by sentiment:', error);
        return of([]);
      })
    );
  }

  // 10. Récupérer les formations triées par ratio de commentaires positifs
  getAllFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/by-positive-ratio`).pipe(
      catchError(error => {
        console.error('Error fetching formations by positive ratio:', error);
        return of([]);
      })
    );
  }

  // 11. Récupérer les formations non archivées triées par ratio de commentaires positifs
  getAllNonArchivedFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees/by-positive-ratio`).pipe(
      catchError(error => {
        console.error('Error fetching non-archived formations by positive ratio:', error);
        return of([]);
      })
    );
  }

  // 11.1 Récupérer les formations non archivées triées par nombre de commentaires positifs
  getAllNonArchivedFormationsByPositiveCount(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/non-archivees/by-positive-count`).pipe(
      catchError(error => {
        console.error('Error fetching non-archived formations by positive count:', error);
        return of([]);
      })
    );
  }

  // 12. Ajouter un commentaire à une formation
  addComment(comment: Comment): Observable<Comment> {
    // Create a DTO object with formationId instead of nested formation object
    const commentDTO = {
      content: comment.content,
      rating: comment.rating,
      userName: comment.userName,
      category: comment.category,
      formationId: comment.formation.id
    };

    console.log('Sending comment DTO:', commentDTO);

    // Send the comment as JSON
    return this.http.post<Comment>(this.commentApiUrl, commentDTO).pipe(
      map(response => {
        console.log('Comment added successfully, response:', response);
        return response;
      }),
      catchError(error => {
        console.error('Error adding comment:', error);
        throw error;
      })
    );
  }

  // 13. Récupérer les commentaires d'une formation
  getCommentsByFormationId(formationId: number, sentiment?: string): Observable<Comment[]> {
    console.log(`Fetching comments for formation ${formationId}${sentiment ? ', sentiment: ' + sentiment : ''}`);

    // Build URL with optional sentiment filter
    const url = sentiment
      ? `${this.commentApiUrl}/formation/${formationId}?sentiment=${sentiment}`
      : `${this.commentApiUrl}/formation/${formationId}`;

    // Get comments as JSON
    return this.http.get<Comment[]>(url).pipe(
      catchError(error => {
        console.error(`Error fetching comments for formation ${formationId}:`, error);
        return of([]);
      })
    );
  }


}