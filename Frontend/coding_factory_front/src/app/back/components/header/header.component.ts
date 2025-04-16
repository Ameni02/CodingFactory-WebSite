import {Component, OnInit} from '@angular/core';
import { NotificationService } from 'src/app/services/notification.service';
import {FeedbackService} from "../../../services/feedback.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit{
  unreadCount: number = 0;
  notifications: any[] = [];
  userId: number = 1;
  constructor(
    private notificationService: NotificationService,
    private feedbackService:FeedbackService

  ) {}

  ngOnInit(): void {

    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });
    this.notificationService.notifications$.subscribe(notifications => {
      this.notifications = notifications;
    });
  }

  markAsRead(notificationId:number) {
    this.feedbackService.markNotificationAsRead(notificationId).subscribe(() => {
      this.notificationService.markAsRead(notificationId);
    });
  }
}
