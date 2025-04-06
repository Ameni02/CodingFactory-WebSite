import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from 'src/app/services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { from, Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-plagiarism-notification',
  template: '',
  styles: [`
    .plagiarism-high {
      background-color: #ffebee;
      color: #c62828;
      .mat-simple-snackbar-action { color: #d32f2f; }
    }
    .plagiarism-medium {
      background-color: #fff8e1;
      color: #ff8f00;
      .mat-simple-snackbar-action { color: #ffa000; }
    }
    .plagiarism-low {
      background-color: #e8f5e9;
      color: #2e7d32;
      .mat-simple-snackbar-action { color: #388e3c; }
    }
  `]
})
export class PlagiarismNotificationComponent implements OnInit, OnDestroy {
  private notificationSub!: Subscription;

  constructor(
    private notificationService: NotificationService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.notificationSub = this.notificationService
      .getPlagiarismNotifications()
      .subscribe({
        next: notification => this.handleNotification(notification),
        error: err => console.error('Notification error:', err)
      });
  }

  private handleNotification(notification: any): void {
    const message = `Plagiarism detected in "${notification.title}" (Score: ${notification.score})`;
    const panelClass = `plagiarism-${notification.verdict}`;
    const action = notification.verdict === 'high' ? 'Review' : 'Dismiss';

    const snackBarRef = this.snackBar.open(message, action, {
      duration: 10000,
      panelClass: [panelClass],
      verticalPosition: 'top',
      horizontalPosition: 'right'
    });

    if (notification.verdict === 'high') {
      snackBarRef.onAction().subscribe(() => {
        this.router.navigate(['/plagiarism-review', notification.deliverableId]);
      });
    }
  }

  ngOnDestroy(): void {
    this.notificationSub?.unsubscribe();
  }
}