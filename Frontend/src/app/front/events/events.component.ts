import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {EventService} from "../../services/event.service";

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['../../../assets/css/style.css','../../../assets/vendor/font-awesome/css/all.min.css','../../../assets/vendor/bootstrap-icons/bootstrap-icons.css','../../../assets/vendor/dropzone/dist/dropzone.css',
    '../../../assets/vendor/flatpickr/dist/flatpickr.css','../../../assets/vendor/choices.js/public/assets/styles/choices.min.css'],
  encapsulation:ViewEncapsulation.None
})
export class EventsComponent implements OnInit{
  events :any[]= [];

  constructor(private eventService: EventService) {
  }

  ngOnInit(): void {
    this.eventService.events$.subscribe(updatedEvents => {
      this.events = updatedEvents;
    });
    this.eventService.getActiveEvents().subscribe();
  }

  // private loadEvents(): void {
  //   this.eventService.getActiveEvents().subscribe({
  //     next: (events: any) => {
  //       console.log(events);
  //       this.events = events;
  //     },
  //     error: (err) => {
  //       console.error('Error fetching events:', err);
  //     }
  //   });
  // }

}
