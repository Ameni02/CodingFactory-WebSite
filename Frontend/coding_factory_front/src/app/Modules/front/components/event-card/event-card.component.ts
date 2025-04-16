import {Component, Input} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import { Router} from "@angular/router";

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.scss']
})
export class EventCardComponent {

  @Input() event: any;
  constructor(private toastr:ToastrService,private router:Router) {
  }
  get eventCover(): string | undefined {
    if (this.event && this.event.imageBase64) {
      return 'data:image/jpg;base64,' + this.event.imageBase64;
    } else if (this.event && this.event.image) {
      // If we have a raw image but not base64
      try {
        // Convert the raw image to base64
        let binary = ''; // Changed from const to let to make it mutable
        const bytes = new Uint8Array(this.event.image);
        const len = bytes.byteLength;
        for (let i = 0; i < len; i++) {
          binary += String.fromCharCode(bytes[i]);
        }
        return 'data:image/jpg;base64,' + window.btoa(binary);
      } catch (error) {
        console.error('Error converting image to base64:', error);
      }
    }
    // Default image if no image is available
    return 'assets/assets/images/events/01.jpg';
  }

  goToDetailEvent(event:any) {
    if (!event || !event.id) {
      this.toastr.error("Event details are missing");
      return;
    }

    console.log('Navigating to event detail:', event);
    this.toastr.success("Navigating to event details");

    // Use the correct path with the events prefix
    this.router.navigate(['/events/eventdetail', event.id], {
      state: { eventData: event }
    });
  }

  markAsInterested(event: any, e: MouseEvent) {
    // Stop event propagation to prevent card click
    e.stopPropagation();
    e.preventDefault();

    // Handle the interested action
    this.toastr.info(`Marked as interested in: ${event.title}`);

    // Here you would typically call a service to update the user's interest in the event
    console.log('User interested in event:', event);
  }

  shareViaDM(event: any, e: MouseEvent) {
    e.stopPropagation();
    this.toastr.info(`Sharing ${event.title} via Direct Message`);
    console.log('Sharing via DM:', event);
  }

  shareToNewsFeed(event: any, e: MouseEvent) {
    e.stopPropagation();
    this.toastr.info(`Sharing ${event.title} to News Feed`);
    console.log('Sharing to news feed:', event);
  }
}
