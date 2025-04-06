import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from 'src/app/services/notification.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-plagiarism-notification',
    template: `<div *ngIf="notification" class="notification-banner">
        {{ notificationMessage }}
    </div>`,
    styles: [`
        .notification-banner {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px;
            background: #4CAF50;
            color: white;
            border-radius: 5px;
            z-index: 1000;
        }
    `]
})
export class PlagiarismNotificationComponent implements OnInit, OnDestroy {
    private notificationSub!: Subscription;
    public notification: any;
    public notificationMessage: string = '';

    constructor(private notificationService: NotificationService) {}

    ngOnInit() {
        this.requestNotificationPermission();
        this.setupWebSocket();
    }

    private requestNotificationPermission() {
        if ('Notification' in window && Notification.permission !== 'granted') {
            Notification.requestPermission().then(permission => {
                console.log('Notification permission:', permission);
            });
        }
    }

    private setupWebSocket() {
        this.notificationSub = this.notificationService.getNotifications().subscribe({
            next: notification => {
                console.log('Received notification:', notification);
                this.notification = notification;
                this.notificationMessage = `Plagiarism result for "${notification.title}": 
                    Score ${notification.score}% (${notification.verdict})`;
                
                this.showBrowserNotification(notification);
            },
            error: err => console.error('Notification error:', err)
        });
    }

    private showBrowserNotification(data: any) {
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification('Plagiarism Analysis Complete', {
                body: `${data.title}\nScore: ${data.score}% (${data.verdict})`,
                icon: '/assets/images/notification-icon.png'
            });
        }
    }

    ngOnDestroy() {
        if (this.notificationSub) {
            this.notificationSub.unsubscribe();
        }
    }
}