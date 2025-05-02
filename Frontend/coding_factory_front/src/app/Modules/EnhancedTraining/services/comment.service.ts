import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://localhost:8057/api/comments';

  constructor(private http: HttpClient) { }

  // Create a new comment
  createComment(comment: Comment): Observable<Comment> {
    return this.http.post<Comment>(this.apiUrl, comment);
  }

  // Get all comments for a formation
  getCommentsByFormationId(formationId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/formation/${formationId}`);
  }

  // Get a comment by ID
  getCommentById(id: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.apiUrl}/${id}`);
  }

  // Update a comment
  updateComment(id: number, comment: Comment): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/${id}`, comment);
  }

  // Delete a comment
  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Batch analyze all comments for a formation
  batchAnalyzeComments(formationId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/formation/${formationId}/analyze`, {});
  }
}
