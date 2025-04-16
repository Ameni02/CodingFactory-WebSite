import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {EventService} from "../../services/event.service";
import {AnimationOptions} from "ngx-lottie";
const toDateOnly = (dateString: string | Date): string => {
  const d = new Date(dateString);
  return d.toISOString().split('T')[0]; // returns '2025-04-17'
};
@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['../../../assets/css/style.css','../../../assets/vendor/font-awesome/css/all.min.css','../../../assets/vendor/bootstrap-icons/bootstrap-icons.css','../../../assets/vendor/dropzone/dist/dropzone.css',
    '../../../assets/vendor/flatpickr/dist/flatpickr.css','../../../assets/vendor/choices.js/public/assets/styles/choices.min.css'],
  encapsulation:ViewEncapsulation.None
})
export class EventsComponent implements OnInit{
  events :any[]= [];
  filteredEvents: any[] = [];
  lottieOptions: AnimationOptions = {
    path: '/assets/no_event.json',
    loop: true,
    autoplay: true,
  };

  constructor(private eventService: EventService) {
  }

  ngOnInit(): void {
    this.eventService.events$.subscribe(updatedEvents => {
      this.events = updatedEvents;
      this.filteredEvents = [...this.events];
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

  onSearch(filters: { genre: string; startDate: string; endDate: string }) {
    console.log('Filters received:', filters);

    const { genre, startDate, endDate } = filters;

    this.filteredEvents = this.events.filter(event => {
      const matchGenre = genre ? event.category?.toLowerCase() === genre.toLowerCase() : true;

      const eventStartDate = toDateOnly(event.startDate);
      const eventEndDate = toDateOnly(event.date);

      let matchStart = true;
      let matchEnd = true;

      if (startDate) {
        const filterStartDate = toDateOnly(startDate);
        matchStart = eventStartDate >= filterStartDate;
      }

      if (endDate) {
        const filterEndDate = toDateOnly(endDate);
        matchEnd = eventEndDate <= filterEndDate;
      }

      return matchGenre && matchStart && matchEnd;
    });

    console.log('Filtered events:', this.filteredEvents);
  }



  resetFilters() {
    this.filteredEvents = [...this.events];
  }
}
