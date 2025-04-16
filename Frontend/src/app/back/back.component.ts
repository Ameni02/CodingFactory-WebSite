import {Component, OnInit} from '@angular/core';
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {ToastrService} from "ngx-toastr";
import {FeedbackService} from "../services/feedback.service";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-back',
  templateUrl: './back.component.html',
  styleUrls: ['./back.component.scss']
})
export class BackComponent implements OnInit{
  socketClient:any=null;
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
    let ws=new SockJS('http://localhost:8090/ws');
    this.socketClient=Stomp.over(ws);
    this.socketClient.connect({},()=>{
      this.notificationSubscription=this.socketClient.subscribe('/topic/notifications',(message:any)=>{
        console.log(message.body)
        const notification = JSON.parse(message.body);
        if(notification)
        {
          this.toastr.info(notification.message, 'Notification');
          this.notificationService.addNotification(notification);
        }

      })

    })

  }

  private loadUnreadNotifications() {
    this.feedbackService.getUnreadNotifications(1).subscribe((notifications) => {
      console.log(notifications)
      this.notificationService.setNotifications(notifications);
    })

  }
}
