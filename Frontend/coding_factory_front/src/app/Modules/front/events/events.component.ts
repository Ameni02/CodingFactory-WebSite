import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {Router} from "@angular/router";
// import {AnimationOptions} from "ngx-lottie";
const toDateOnly = (dateString: string | Date): string => {
  const d = new Date(dateString);
  return d.toISOString().split('T')[0]; // returns '2025-04-17'
};
@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EventsComponent implements OnInit{
  events: any[] = [];
  filteredEvents: any[] = [];
  event: any = {};
  selectedGenre: string = '';
  startDate: string = '';
  endDate: string = '';
  categories = [
    'CONFERENCE',
    'WORKSHOP',
    'WEBINAR',
    'CONCERT',
    'FESTIVAL',
    'SPORT',
    'NETWORKING',
    'CHARITY',
    'EXHIBITION'
  ];
  lottieOptions: any = {
    path: '/assets/assets/no_event.json',
    loop: true,
    autoplay: true,
  };

  constructor(private eventService: EventService, private router: Router) {
  }

  ngOnInit(): void {
    // Subscribe to events stream
    this.eventService.events$.subscribe(updatedEvents => {
      this.events = updatedEvents;
      this.filteredEvents = [...this.events];
      console.log('Received events from stream:', this.events);
    });

    // Fetch active events
    this.fetchEvents();

    // Debug: Check event counts
    this.eventService.getEventCounts().subscribe({
      next: (counts) => {
        console.log('Event counts:', counts);
      },
      error: (error) => {
        console.error('Error fetching event counts:', error);
      }
    });

    // Debug: Check event images
    this.eventService.checkEventImages().subscribe({
      next: (imageInfo) => {
        console.log('Event image info:', imageInfo);
      },
      error: (error) => {
        console.error('Error checking event images:', error);
      }
    });
  }

  private fetchEvents(): void {
    this.eventService.getActiveEvents().subscribe({
      next: (events) => {
        console.log('Successfully fetched events:', events);
        if (events.length === 0) {
          console.warn('No events returned from API');
        }
      },
      error: (error) => {
        console.error('Error fetching events:', error);
      }
    });
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

  viewEventDetails(event: any) {
    // Navigate to event details page
    console.log('View event details:', event);
    this.router.navigate(['/events/eventdetail', event.id], { state: { eventData: event } });
  }

  submitEvent() {
    if (this.event.title && this.event.description && this.event.date &&
      this.event.location && this.event.category &&
      this.event.maxParticipants >= 1 && this.event.price >= 0) {

      const eventRequest = {
        title: this.event.title,
        description: this.event.description,
        date: this.event.date,
        location: this.event.location,
        category: this.event.category,
        maxParticipants: this.event.maxParticipants,
        price: this.event.price
      };

      this.eventService.saveEvent(eventRequest).subscribe({
        next: (response) => {
          console.log('Event created successfully:', response);
          this.resetEventForm();
          // Close the modal
          const modalElement = document.getElementById('modalCreateEvents');
          if (modalElement) {
            // Use Bootstrap's modal API to hide the modal
            // (modalElement as any).modal('hide');
          }
        },
        error: (error) => {
          console.error('Error saving event:', error);
        }
      });
    }
  }

  private resetEventForm() {
    this.event = {
      title: '',
      description: '',
      date: '',
      location: '',
      category: '',
      maxParticipants: 0,
      price: 0.0
    };
  }
}
