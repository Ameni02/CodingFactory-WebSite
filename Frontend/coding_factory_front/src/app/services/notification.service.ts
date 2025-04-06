import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, Observer, EMPTY, Subject, timer } from 'rxjs';
import { catchError, tap, retryWhen, delayWhen, switchAll } from 'rxjs/operators';

export interface PlagiarismNotification {
  deliverableId: number;
  title: string;
  score: number;
  verdict: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private socket$!: WebSocketSubject<PlagiarismNotification>;
  private readonly WS_URL = 'ws://localhost:8080/pfespace/ws';
  private connectionStatus$ = new Subject<boolean>();
  private retryConfig = {
    initialDelay: 1000,
    maxDelay: 5000,
    maxRetries: 5
  };

  constructor() {
    this.connect();
  }

  private connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket<PlagiarismNotification>({
        url: this.WS_URL,
        openObserver: {
          next: () => {
            console.log('WebSocket connection established');
            this.connectionStatus$.next(true);
          }
        },
        closeObserver: {
          next: () => {
            console.log('WebSocket connection closed');
            this.connectionStatus$.next(false);
            this.retryConnection();
          }
        },
        deserializer: msg => {
          try {
            return JSON.parse(msg.data);
          } catch (e) {
            console.error('Error parsing WebSocket message:', e);
            return null;
          }
        }
      });

      this.socket$.pipe(
        catchError(error => {
          console.error('WebSocket error:', error);
          this.retryConnection();
          return EMPTY;
        })
      ).subscribe();
    }
  }

  private retryConnection(): void {
    timer(this.retryConfig.initialDelay).subscribe(() => {
      console.log('Attempting to reconnect WebSocket...');
      this.connect();
    });
  }

  public getNotifications(): Observable<PlagiarismNotification> {
    return new Observable((observer: Observer<PlagiarismNotification>) => {
      const subscription = this.socket$.subscribe({
        next: (notification) => {
          if (notification) {
            observer.next(notification);
          }
        },
        error: (err) => observer.error(err),
        complete: () => observer.complete()
      });

      return () => subscription.unsubscribe();
    }).pipe(
      retryWhen(errors => errors.pipe(
        tap(err => console.log('Retrying WebSocket connection due to:', err)),
        delayWhen(() => timer(this.retryConfig.initialDelay))
      ))
    );
  }

  public getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus$.asObservable();
  }

  public close(): void {
    if (this.socket$) {
      this.socket$.complete();
      this.connectionStatus$.complete();
    }
  }
}