import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<any[]>([]);
  private unreadCountSubject = new BehaviorSubject<number>(0);

  notifications$ = this.notificationsSubject.asObservable();
  unreadCount$ = this.unreadCountSubject.asObservable();

  constructor() { }

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
