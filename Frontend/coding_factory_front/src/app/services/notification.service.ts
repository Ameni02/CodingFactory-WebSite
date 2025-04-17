import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<any[]>([]);
  private unreadCountSubject = new BehaviorSubject<number>(0);

  notifications$ = this.notificationsSubject.asObservable();
  unreadCount$ = this.unreadCountSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api'; // Update with your actual API URL

  constructor(private http: HttpClient) { }

  /**
   * Sends an email notification
   * @param to Recipient email address
   * @param subject Email subject
   * @param body Email body content
   * @returns Observable of the email sending operation
   */
  sendEmail(to: string, subject: string, body: string): Observable<any> {
    // If you have an actual email API endpoint, use this:
    // return this.http.post(`${this.apiUrl}/notifications/email`, {
    //   to,
    //   subject,
    //   body
    // });

    // For now, we'll simulate a successful email send with a delay
    console.log('Sending email to:', to, 'Subject:', subject);
    return of({ success: true, message: 'Email sent successfully' });
  }

  setNotifications(notifications: any[]) {
    this.notificationsSubject.next(notifications);
    this.updateUnreadCount();
  }

  addNotification(notification: any) {
    const currentNotifications = this.notificationsSubject.value;
    const updatedNotifications = [notification, ...currentNotifications];
    this.notificationsSubject.next(updatedNotifications);
    this.updateUnreadCount();
  }

  markAsRead(notificationId: number) {
    const currentNotifications = this.notificationsSubject.value;
    const updatedNotifications = currentNotifications.map(notification => {
      if (notification.id === notificationId) {
        return { ...notification, seen: true };
      }
      return notification;
    });
    this.notificationsSubject.next(updatedNotifications);
    this.updateUnreadCount();
  }

  private updateUnreadCount() {
    const unreadCount = this.notificationsSubject.value.filter(notification => !notification.seen).length;
    this.unreadCountSubject.next(unreadCount);
  }
}
