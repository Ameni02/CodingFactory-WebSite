import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable, catchError, tap, throwError } from "rxjs";

interface RatingRequest {
  eventId: number;
  rating: number;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class EventRatingService {
  private apiUrl = `${environment.apiUrl}rating`;

  constructor(private http: HttpClient) {}

  addRating(eventId: number, rating: number, userId: number): Observable<any> {
    // Convert to the format expected by the backend
    const requestPayload = {
      eventId: Number(eventId),
      rating: Number(rating),
      userId: Number(userId)
    };

    console.log('Sending rating request:', requestPayload);
    console.log('To URL:', `${this.apiUrl}/${eventId}`);

    // Set headers to ensure proper content type
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Match the exact endpoint structure from the backend controller
    // Use responseType: 'text' to handle the response as text instead of JSON
    return this.http.post(`${this.apiUrl}/${eventId}`, requestPayload, {
      headers,
      responseType: 'text'
    }).pipe(
      tap(response => console.log('Rating added successfully:', response)),
      catchError(error => {
        console.error('Error adding rating:', error);
        console.error('Error response body:', error.error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        return throwError(() => error);
      })
    );
  }

  getRatings(eventId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${eventId}/get-ratings`).pipe(
      tap(ratings => console.log('Ratings retrieved:', ratings)),
      catchError(error => {
        console.error('Error getting ratings:', error);
        return throwError(() => error);
      })
    );
  }

  // Check if user has already rated the event
  hasUserRated(eventId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check/${eventId}/${userId}`).pipe(
      catchError(error => {
        console.error('Error checking if user has rated:', error);
        // If there's an error, assume the user hasn't rated
        return throwError(() => false);
      })
    );
  }

  // Alternative method to add rating using PUT instead of POST
  updateRating(eventId: number, rating: number, userId: number): Observable<any> {
    const requestPayload = {
      eventId: Number(eventId),
      rating: Number(rating),
      userId: Number(userId)
    };

    console.log('Updating rating with request:', requestPayload);

    // Set headers to ensure proper content type
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Use responseType: 'text' to handle the response as text instead of JSON
    return this.http.put(`${this.apiUrl}/ratings`, requestPayload, {
      headers,
      responseType: 'text'
    }).pipe(
      tap(response => console.log('Rating updated successfully:', response)),
      catchError(error => {
        console.error('Error updating rating:', error);
        console.error('Error response body:', error.error);
        return throwError(() => error);
      })
    );
  }

  getAverageRating(eventId: number): Observable<any> {
    console.log('Getting average rating for event:', eventId);
    console.log('From URL:', `${this.apiUrl}/${eventId}/average-rating`);

    return this.http.get(`${this.apiUrl}/${eventId}/average-rating`).pipe(
      tap(avgRating => console.log('Average rating retrieved:', avgRating)),
      catchError(error => {
        console.error('Error getting average rating:', error);
        // Return 0 as default rating if there's an error
        return throwError(() => error);
      })
    );
  }

  // Combined method to handle both adding and updating ratings
  submitRating(eventId: number, rating: number, userId: number): Observable<any> {
    const requestPayload = {
      eventId: Number(eventId),
      rating: Number(rating),
      userId: Number(userId)
    };

    console.log('Submitting rating with request:', requestPayload);

    // First try to update, and if that fails with a 404, then add a new rating
    return this.updateRating(eventId, rating, userId).pipe(
      catchError(error => {
        if (error.status === 404 || error.status === 400) {
          console.log('No existing rating found, adding new rating instead');
          return this.addRating(eventId, rating, userId);
        }
        return throwError(() => error);
      })
    );
  }
}
