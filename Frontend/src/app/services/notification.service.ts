import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private unreadCount = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCount.asObservable();

  private notifications = new BehaviorSubject<any[]>([]);
  notifications$ = this.notifications.asObservable();

  constructor() {}

  setNotifications(notifications: any[]) {
    this.notifications.next(notifications);
    this.unreadCount.next(notifications.length);
  }


  addNotification(notification: any) {
    const currentNotifications = this.notifications.getValue();
    const updatedNotifications = [notification, ...currentNotifications];
    this.notifications.next(updatedNotifications);
    this.unreadCount.next(updatedNotifications.length);
  }

  markAsRead(notificationId: number) {
    const currentNotifications = this.notifications.getValue();
    const updatedNotifications = currentNotifications.filter(n => n.id !== notificationId);
    this.notifications.next(updatedNotifications);
    this.unreadCount.next(updatedNotifications.length);
  }
}
