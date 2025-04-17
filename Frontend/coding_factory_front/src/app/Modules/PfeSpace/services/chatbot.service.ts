import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PfeSpaceConfig } from '../config/pfe-space.config';

export interface ChatResponse {
  responseText: string;
  suggestedQuestions: string[];
  intent: string;
  timestamp: string;
  requiresAction: boolean;
  actionType: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = 'http://localhost:8080/pfespace/api/chatbot';

  constructor(private http: HttpClient) {}

  /**
   * Send a message to the chatbot and get a response
   * @param message The user's message
   * @returns Observable with the chatbot response
   */
  sendMessage(message: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/ask`, { message })
      .pipe(
        catchError(error => {
          console.error('Error sending message to chatbot:', error);
          throw error;
        })
      );
  }

  /**
   * Get a list of available intents (for testing purposes)
   * @returns Observable with the list of intents
   */
  getIntents(): Observable<Record<string, string>> {
    return this.http.get<Record<string, string>>(`${this.apiUrl}/intents`)
      .pipe(
        catchError(error => {
          console.error('Error getting chatbot intents:', error);
          throw error;
        })
      );
  }
}
