import {Component, OnDestroy, OnInit} from '@angular/core';
import * as SockJS from "sockjs-client";
import { Client } from '@stomp/stompjs';
import {ToastrService} from "ngx-toastr";
import { FeedbackService } from '../services/feedback.service';
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-back',
  templateUrl: './back.component.html',
  styleUrls: ['./back.component.scss']
})
export class BackComponent implements OnInit, OnDestroy {
  socketClient: Client | null = null;
  private notificationSubscription: any;
  constructor(private toastr:ToastrService,
              private feedbackService:FeedbackService,
              private notificationService:NotificationService) {
  }

  ngOnInit(): void {
    this.loadUnreadNotifications();
    this.initializeWebSocketConnection();

  }


  private initializeWebSocketConnection() {
    try {
      this.socketClient = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8090/ws'),
        debug: () => {},
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });

      this.socketClient.onConnect = () => {
        console.log('Connected to WebSocket server');
        this.notificationSubscription = this.socketClient!.subscribe('/topic/notifications', (message) => {
          console.log('Received notification:', message.body);
          try {
            const notification = JSON.parse(message.body);
            if (notification) {
              this.toastr.info(notification.message, 'Notification');
              this.notificationService.addNotification(notification);
            }
          } catch (error) {
            console.error('Error parsing notification:', error);
          }
        });
      };

      this.socketClient.onStompError = (frame) => {
        console.error('WebSocket connection error:', frame);
        this.toastr.warning('Could not connect to notification service', 'Connection Error');
      };

      this.socketClient.activate();
    } catch (error) {
      console.error('Error initializing WebSocket:', error);
      this.toastr.warning('Could not initialize notification service', 'Connection Error');
    }
  }

  private loadUnreadNotifications() {
    this.feedbackService.getUnreadNotifications(1).subscribe({
      next: (notifications) => {
        console.log('Loaded notifications:', notifications);
        this.notificationService.setNotifications(notifications);
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.toastr.error('Failed to load notifications', 'Error');
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up WebSocket connection when component is destroyed
    if (this.notificationSubscription) {
      try {
        this.notificationSubscription.unsubscribe();
      } catch (error) {
        console.error('Error unsubscribing from notifications:', error);
      }
    }

    if (this.socketClient && this.socketClient.active) {
      try {
        this.socketClient.deactivate();
      } catch (error) {
        console.error('Error disconnecting WebSocket:', error);
      }
    }
  }
}
