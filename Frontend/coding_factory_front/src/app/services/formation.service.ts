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
}

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private apiUrl = 'http://localhost:8057/api/formations'; // modifie l'URL si besoin
  private commentApiUrl = 'http://localhost:8057/api/comments';

  constructor(private http: HttpClient) {}

  // ✅ 1. Récupérer toutes les formations
  getAllFormations(): Observable<Formation[]> {
    return this.http.get(`${this.apiUrl}`, {
      headers: { 'Accept': 'application/json' },
      responseType: 'text'
    }).pipe(
      map(response => {
        try {
          // Try to parse as JSON
          if (response && response.trim().startsWith('{') || response.trim().startsWith('[')) {
            return JSON.parse(response) as Formation[];
          } else {
            // If it's not JSON (likely XML), log and return empty array
            console.warn('Response is not JSON. Returning empty array.');
            return [] as Formation[];
          }
        } catch (e) {
          console.error('Error parsing response:', e);
          return [] as Formation[];
        }
      }),
      catchError(error => {
        console.error('Error fetching all formations:', error);
        return of([]);
      })
    );
  }

  // ✅ 2. Récupérer uniquement les formations non archivées
  getAllFormationsNonArchivees(): Observable<Formation[]> {
    // Use XML response type since the API seems to return XML
    return this.http.get(`${this.apiUrl}/non-archivees`, {
      responseType: 'text',
      headers: { 'Accept': 'application/xml' }
    }).pipe(
      map(response => {
        console.log('Raw response received');

        // Try to parse XML response
        try {
          const parser = new DOMParser();
          const xmlDoc = parser.parseFromString(response, 'text/xml');

          // Extract formations from XML
          const formationNodes = xmlDoc.getElementsByTagName('item');
          const formations: Formation[] = [];

          for (let i = 0; i < formationNodes.length; i++) {
            const node = formationNodes[i];
            const formation: Formation = {
              id: parseInt(node.getElementsByTagName('id')[0]?.textContent || '0'),
              titre: node.getElementsByTagName('titre')[0]?.textContent || '',
              pdfFileName: node.getElementsByTagName('pdfFileName')[0]?.textContent || '',
              archived: node.getElementsByTagName('archived')[0]?.textContent === 'true'
            };

            // Try to get comment count if available
            const commentsNode = node.getElementsByTagName('comments')[0];
            if (commentsNode) {
              const commentItems = commentsNode.getElementsByTagName('comments');
              formation.totalCommentCount = commentItems.length;
            }

            formations.push(formation);
          }

          console.log(`Parsed ${formations.length} formations from XML`);
          return formations;
        } catch (e) {
          console.error('Error parsing XML response:', e);
          return [];
        }
      }),
      catchError(error => {
        console.error('Error fetching non-archived formations:', error);
        return of([]);
      })
    );
  }

  // ✅ 3. Récupérer une formation par ID
  getFormationById(id: number): Observable<Formation> {
    return this.http.get(`${this.apiUrl}/${id}`, {
      responseType: 'text',
      headers: { 'Accept': 'application/xml' }
    }).pipe(
      map(response => {
        console.log('Raw formation response received');

        // Try to parse XML response
        try {
          const parser = new DOMParser();
          const xmlDoc = parser.parseFromString(response, 'text/xml');

          // Extract formation from XML
          const formation: Formation = {
            id: parseInt(xmlDoc.getElementsByTagName('id')[0]?.textContent || '0'),
            titre: xmlDoc.getElementsByTagName('titre')[0]?.textContent || '',
            pdfFileName: xmlDoc.getElementsByTagName('pdfFileName')[0]?.textContent || '',
            archived: xmlDoc.getElementsByTagName('archived')[0]?.textContent === 'true'
          };

          // Try to get comments if available
          const commentsNode = xmlDoc.getElementsByTagName('comments')[0];
          if (commentsNode) {
            const commentItems = commentsNode.getElementsByTagName('comments');
            formation.totalCommentCount = commentItems.length;

            // Parse comments
            const comments: Comment[] = [];
            for (let i = 0; i < commentItems.length; i++) {
              const commentNode = commentItems[i];
              const comment: Comment = {
                id: parseInt(commentNode.getElementsByTagName('id')[0]?.textContent || '0'),
                content: commentNode.getElementsByTagName('content')[0]?.textContent || '',
                rating: parseInt(commentNode.getElementsByTagName('rating')[0]?.textContent || '0'),
                userName: commentNode.getElementsByTagName('userName')[0]?.textContent || '',
                category: commentNode.getElementsByTagName('category')[0]?.textContent || '',
                formation: { id: formation.id }
              };

              // Try to get sentiment info
              const sentimentLabel = commentNode.getElementsByTagName('sentimentLabel')[0]?.textContent;
              const sentimentScore = commentNode.getElementsByTagName('sentimentScore')[0]?.textContent;

              if (sentimentLabel) {
                comment.sentimentLabel = sentimentLabel;
              }

              if (sentimentScore) {
                comment.sentimentScore = parseFloat(sentimentScore);
              }

              comments.push(comment);
            }

            formation.comments = comments;
          }

          // Try to get sentiment metrics
          const avgSentiment = xmlDoc.getElementsByTagName('averageSentimentScore')[0]?.textContent;
          const posRatio = xmlDoc.getElementsByTagName('positiveCommentRatio')[0]?.textContent;

          if (avgSentiment) {
            formation.averageSentimentScore = parseFloat(avgSentiment);
          }

          if (posRatio) {
            formation.positiveCommentRatio = parseFloat(posRatio);
          }

          console.log('Parsed formation from XML:', formation);
          return formation;
        } catch (e) {
          console.error('Error parsing XML formation response:', e);
          throw e;
        }
      }),
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
    return this.http.get(`${this.apiUrl}/by-sentiment`, {
      headers: { 'Accept': 'application/json' },
      responseType: 'text'
    }).pipe(
      map(response => {
        try {
          // Try to parse as JSON
          if (response && response.trim().startsWith('{') || response.trim().startsWith('[')) {
            return JSON.parse(response) as Formation[];
          } else {
            // If it's not JSON (likely XML), log and return empty array
            console.warn('Response is not JSON. Returning empty array.');
            return [] as Formation[];
          }
        } catch (e) {
          console.error('Error parsing response:', e);
          return [] as Formation[];
        }
      }),
      catchError(error => {
        console.error('Error fetching formations by sentiment:', error);
        return of([]);
      })
    );
  }

  // 9. Récupérer les formations non archivées triées par score de sentiment
  getAllNonArchivedFormationsBySentiment(): Observable<Formation[]> {
    return this.http.get(`${this.apiUrl}/non-archivees/by-sentiment`, {
      headers: { 'Accept': 'application/json' },
      responseType: 'text'
    }).pipe(
      map(response => {
        try {
          // Try to parse as JSON
          if (response && response.trim().startsWith('{') || response.trim().startsWith('[')) {
            return JSON.parse(response) as Formation[];
          } else {
            // If it's not JSON (likely XML), log and return empty array
            console.warn('Response is not JSON. Returning empty array.');
            return [] as Formation[];
          }
        } catch (e) {
          console.error('Error parsing response:', e);
          return [] as Formation[];
        }
      }),
      catchError(error => {
        console.error('Error fetching non-archived formations by sentiment:', error);
        return of([]);
      })
    );
  }

  // 10. Récupérer les formations triées par ratio de commentaires positifs
  getAllFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get(`${this.apiUrl}/by-positive-ratio`, {
      headers: { 'Accept': 'application/json' },
      responseType: 'text'
    }).pipe(
      map(response => {
        try {
          // Try to parse as JSON
          if (response && response.trim().startsWith('{') || response.trim().startsWith('[')) {
            return JSON.parse(response) as Formation[];
          } else {
            // If it's not JSON (likely XML), log and return empty array
            console.warn('Response is not JSON. Returning empty array.');
            return [] as Formation[];
          }
        } catch (e) {
          console.error('Error parsing response:', e);
          return [] as Formation[];
        }
      }),
      catchError(error => {
        console.error('Error fetching formations by positive ratio:', error);
        return of([]);
      })
    );
  }

  // 11. Récupérer les formations non archivées triées par ratio de commentaires positifs
  getAllNonArchivedFormationsByPositiveRatio(): Observable<Formation[]> {
    return this.http.get(`${this.apiUrl}/non-archivees/by-positive-ratio`, {
      headers: { 'Accept': 'application/json' },
      responseType: 'text'
    }).pipe(
      map(response => {
        try {
          // Try to parse as JSON
          if (response && response.trim().startsWith('{') || response.trim().startsWith('[')) {
            return JSON.parse(response) as Formation[];
          } else {
            // If it's not JSON (likely XML), log and return empty array
            console.warn('Response is not JSON. Returning empty array.');
            return [] as Formation[];
          }
        } catch (e) {
          console.error('Error parsing response:', e);
          return [] as Formation[];
        }
      }),
      catchError(error => {
        console.error('Error fetching non-archived formations by positive ratio:', error);
        return of([]);
      })
    );
  }

  // 12. Ajouter un commentaire à une formation
  addComment(comment: Comment): Observable<Comment> {
    // Convert the comment to XML format
    const xmlComment = `
      <comment>
        <content>${comment.content}</content>
        <rating>${comment.rating}</rating>
        <userName>${comment.userName}</userName>
        <category>${comment.category}</category>
        <formation>
          <id>${comment.formation.id}</id>
        </formation>
      </comment>
    `;

    // Send the comment as XML
    return this.http.post(this.commentApiUrl, xmlComment, {
      headers: {
        'Accept': 'application/xml',
        'Content-Type': 'application/xml'
      },
      responseType: 'text'
    }).pipe(
      map(response => {
        console.log('Comment added successfully, response:', response);

        // Try to parse XML response
        try {
          const parser = new DOMParser();
          const xmlDoc = parser.parseFromString(response, 'text/xml');

          // Extract comment from XML
          const newComment: Comment = {
            id: parseInt(xmlDoc.getElementsByTagName('id')[0]?.textContent || '0'),
            content: xmlDoc.getElementsByTagName('content')[0]?.textContent || comment.content,
            rating: parseInt(xmlDoc.getElementsByTagName('rating')[0]?.textContent || comment.rating.toString()),
            userName: xmlDoc.getElementsByTagName('userName')[0]?.textContent || comment.userName,
            category: xmlDoc.getElementsByTagName('category')[0]?.textContent || comment.category || '',
            formation: comment.formation
          };

          // Try to get created date
          const createdAtStr = xmlDoc.getElementsByTagName('createdAt')[0]?.textContent;
          if (createdAtStr) {
            newComment.createdAt = new Date(createdAtStr);
          } else {
            newComment.createdAt = new Date();
          }

          // Try to get sentiment info
          const sentimentLabel = xmlDoc.getElementsByTagName('sentimentLabel')[0]?.textContent;
          const sentimentScore = xmlDoc.getElementsByTagName('sentimentScore')[0]?.textContent;

          if (sentimentLabel) {
            newComment.sentimentLabel = sentimentLabel;
          }

          if (sentimentScore) {
            newComment.sentimentScore = parseFloat(sentimentScore);
          }

          return newComment;
        } catch (e) {
          console.error('Error parsing XML comment response:', e);

          // If parsing fails, return the original comment with a generated ID
          return {
            ...comment,
            id: new Date().getTime(),
            createdAt: new Date()
          };
        }
      }),
      catchError(error => {
        // Check if it's a 201 Created status (which is success but Angular treats as error)
        if (error.status === 201 && error.error && typeof error.error === 'string') {
          console.log('Comment created successfully with 201 status');

          try {
            // Try to parse the error body as XML
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(error.error, 'text/xml');

            // Extract comment ID from response if available
            const idElement = xmlDoc.getElementsByTagName('id')[0];
            const id = idElement ? parseInt(idElement.textContent || '0') : new Date().getTime();

            return of({
              ...comment,
              id: id,
              createdAt: new Date()
            });
          } catch (e) {
            console.error('Error parsing 201 response:', e);
          }
        }

        console.error('Error adding comment:', error);
        throw error;
      })
    );
  }

  // 13. Récupérer les commentaires d'une formation
  getCommentsByFormationId(formationId: number): Observable<Comment[]> {
    console.log(`Fetching comments for formation ${formationId}`);

    // First try with JSON response type
    return this.http.get<Comment[]>(`${this.commentApiUrl}/formation/${formationId}`).pipe(
      map(comments => {
        console.log('Received JSON comments response:', comments);
        return comments;
      }),
      catchError(jsonError => {
        console.log('Error with JSON request, trying XML format:', jsonError);

        // If JSON fails, try with XML response type
        return this.http.get(`${this.commentApiUrl}/formation/${formationId}`, {
          responseType: 'text',
          headers: { 'Accept': 'application/xml' }
        }).pipe(
          map(response => {
            console.log('Raw XML comments response received:', response.substring(0, 200) + '...');

            // Try to parse XML response
            try {
              const parser = new DOMParser();
              const xmlDoc = parser.parseFromString(response, 'text/xml');

              // Log the XML structure to help debug
              console.log('XML document:', xmlDoc);

              // Try different tag names that might be used in the XML
              let commentNodes = xmlDoc.getElementsByTagName('item');
              if (commentNodes.length === 0) {
                commentNodes = xmlDoc.getElementsByTagName('comment');
              }
              if (commentNodes.length === 0) {
                commentNodes = xmlDoc.getElementsByTagName('comments');
              }

              console.log(`Found ${commentNodes.length} comment nodes`);

              const comments: Comment[] = [];

              for (let i = 0; i < commentNodes.length; i++) {
                const node = commentNodes[i];
                console.log(`Processing comment node ${i}:`, node);

                // Try to extract comment data
                try {
                  const comment: Comment = {
                    id: parseInt(this.getXmlNodeContent(node, 'id') || '0'),
                    content: this.getXmlNodeContent(node, 'content') || '',
                    rating: parseInt(this.getXmlNodeContent(node, 'rating') || '0'),
                    userName: this.getXmlNodeContent(node, 'userName') || '',
                    category: this.getXmlNodeContent(node, 'category') || '',
                    formation: {
                      id: formationId
                    }
                  };

                  // Try to get created date
                  const createdAtStr = this.getXmlNodeContent(node, 'createdAt');
                  if (createdAtStr) {
                    comment.createdAt = new Date(createdAtStr);
                  }

                  // Try to get sentiment info
                  const sentimentLabel = this.getXmlNodeContent(node, 'sentimentLabel');
                  const sentimentScore = this.getXmlNodeContent(node, 'sentimentScore');

                  if (sentimentLabel) {
                    comment.sentimentLabel = sentimentLabel;
                  }

                  if (sentimentScore) {
                    comment.sentimentScore = parseFloat(sentimentScore);
                  }

                  console.log('Parsed comment:', comment);
                  comments.push(comment);
                } catch (nodeError) {
                  console.error('Error parsing comment node:', nodeError);
                }
              }

              console.log(`Parsed ${comments.length} comments from XML`);
              return comments;
            } catch (e) {
              console.error('Error parsing XML comments response:', e);
              return [];
            }
          }),
          catchError(xmlError => {
            console.error(`Error fetching comments as XML for formation ${formationId}:`, xmlError);
            return of([]);
          })
        );
      })
    );
  }

  // Helper method to get content from XML node
  private getXmlNodeContent(node: Element, tagName: string): string | null {
    const elements = node.getElementsByTagName(tagName);
    if (elements.length > 0) {
      return elements[0].textContent;
    }
    return null;
  }
}