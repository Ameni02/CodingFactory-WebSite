import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, Subject, timer, EMPTY } from 'rxjs';
import { catchError, tap, retryWhen, delayWhen } from 'rxjs/operators';

export interface PlagiarismNotification {
  deliverableId: number;
  title: string;
  score: number;
  verdict: 'high' | 'medium' | 'low';
  timestamp: string;
}

export interface DeliverableNotification {
  deliverableId: number;
  title: string;
  status: 'submitted' | 'processed' | 'error';
  reportUrl?: string;
  timestamp: string;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private plagiarismSocket$!: WebSocketSubject<PlagiarismNotification>;
  private deliverableSocket$!: WebSocketSubject<DeliverableNotification>;
  private readonly WS_ENDPOINT = 'ws://localhost:8080/pfespace/ws';
  private connectionStatus$ = new Subject<boolean>();
  
  private readonly retryConfig = {
    initialDelay: 1000,
    maxRetries: 5,
    maxDelay: 10000
  };

  constructor() {
    console.log('Initializing NotificationService');
    this.initializeConnections();
  }

  private initializeConnections(): void {
    console.log('Initializing WebSocket connections');
    this.connectPlagiarism();
    this.connectDeliverable();
  }

  private connectPlagiarism(): void {
    if (!this.plagiarismSocket$ || this.plagiarismSocket$.closed) {
      console.log('Connecting to plagiarism notifications');
      this.plagiarismSocket$ = this.createWebSocket<PlagiarismNotification>('plagiarism');
    }
  }

  private connectDeliverable(): void {
    if (!this.deliverableSocket$ || this.deliverableSocket$.closed) {
      console.log('Connecting to deliverable notifications');
      this.deliverableSocket$ = this.createWebSocket<DeliverableNotification>('deliverables');
    }
  }

  private createWebSocket<T>(path: string): WebSocketSubject<T> {
    const url = `${this.WS_ENDPOINT}/${path}`;
    console.log(`Creating WebSocket connection to: ${url}`);
    
    const socket$ = webSocket<T>({
      url: url,
      openObserver: {
        next: () => {
          console.log(`Connected to ${path} notifications`);
          this.connectionStatus$.next(true);
        }
      },
      closeObserver: {
        next: () => {
          console.log(`Disconnected from ${path} notifications`);
          this.connectionStatus$.next(false);
          this.retryConnection(path);
        }
      }
    });

    socket$.pipe(
      tap(message => console.log(`Received ${path} notification:`, message)),
      catchError(error => {
        console.error(`${path} WebSocket error:`, error);
        this.retryConnection(path);
        return EMPTY;
      })
    ).subscribe();

    return socket$;
  }

  private retryConnection(path: string): void {
    console.log(`Scheduling retry for ${path} connection`);
    timer(this.retryConfig.initialDelay).subscribe(() => {
      console.log(`Reconnecting to ${path}...`);
      if (path === 'plagiarism') {
        this.connectPlagiarism();
      } else {
        this.connectDeliverable();
      }
    });
  }

  public getPlagiarismNotifications(): Observable<PlagiarismNotification> {
    console.log('Getting plagiarism notifications');
    return this.plagiarismSocket$.pipe(
      tap(notification => console.log('Plagiarism notification received:', notification)),
      retryWhen(errors => errors.pipe(
        tap(err => console.log('Retrying plagiarism connection:', err)),
        delayWhen(() => timer(this.retryConfig.initialDelay))
      ))
    );
  }

  public getDeliverableNotifications(): Observable<DeliverableNotification> {
    console.log('Getting deliverable notifications');
    return this.deliverableSocket$.pipe(
      tap(notification => console.log('Deliverable notification received:', notification)),
      retryWhen(errors => errors.pipe(
        tap(err => console.log('Retrying deliverables connection:', err)),
        delayWhen(() => timer(this.retryConfig.initialDelay))
      ))
    );
  }

  public getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus$.asObservable();
  }

  ngOnDestroy() {
    console.log('Cleaning up notification service');
    this.plagiarismSocket$?.complete();
    this.deliverableSocket$?.complete();
    this.connectionStatus$.complete();
  }
}