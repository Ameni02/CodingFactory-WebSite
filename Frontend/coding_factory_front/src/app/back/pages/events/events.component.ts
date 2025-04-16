import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit{
  @ViewChild('updateModal') updateModal!: ElementRef;
  events: any[] = [];
  selectedEvent: any = null;
  eventForm: FormGroup;
  selectedEventCover:any;
  selectedPicture: string | undefined;
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


  constructor(
    private eventService: EventService,
    private toastr: ToastrService,
    private router: Router,
    private fb: FormBuilder

  ) {

    this.eventForm = this.fb.group({
      id: [''],
      title: ['', Validators.required],
      date: ['', Validators.required],
      location: ['', Validators.required],
      description: ['', Validators.required],
      status: ['', Validators.required],
      category: ['', Validators.required],
      maxParticipants: ['', [Validators.required, Validators.min(1)]],
      price: ['', [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents() {
    this.eventService.getEvents().subscribe({
      next: (events) => {
        this.events = events;
      },
      error: (error) => {
        this.toastr.error('Failed to load events.', 'Error');
      }
    });
  }

  showParticipants(eventId: number) {
    // Check if we're in the admin layout
    if (this.router.url.includes('/admin/')) {
      this.router.navigate(['/admin/events/participations', eventId]);
    } else {
      this.router.navigate(['/back/participations', eventId]);
    }
  }

  showFeedback(eventId: number) {
    // Check if we're in the admin layout
    if (this.router.url.includes('/admin/')) {
      this.router.navigate(['/admin/events/feedbacks', eventId]);
    } else {
      this.router.navigate(['/back/feedbacks', eventId]);
    }
  }

  editEvent(event: any) {
    this.selectedEvent = event;
    this.eventForm.patchValue(event);
    const modalElement = document.getElementById('updateModal');
    if (modalElement) {
      modalElement.classList.add('show');
      modalElement.style.display = 'block';
      document.body.classList.add('modal-open');
    }
  }

  updateEvent() {
    if (this.eventForm.valid) {
      const eventData = this.eventForm.value;

      this.eventService.updateEvent(eventData).subscribe(() => {
        if (this.selectedEventCover) {
          this.uploadEventPicture(eventData.id, this.selectedEventCover);
          this.toastr.success('Event image updated successfully!', 'Success');
        } else {
          this.loadEvents();
          this.closeModal();
        }
      });
    }
  }

  closeModal() {
    const modalElement = document.getElementById('updateModal');
    if (modalElement) {
      modalElement.classList.remove('show');
      modalElement.style.display = 'none';
      document.body.classList.remove('modal-open');
    }
    const modalBackdrop = document.querySelector('.modal-backdrop');
    if (modalBackdrop) {
      modalBackdrop.remove();
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedEventCover = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedPicture = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  uploadEventPicture(eventId: number, file: File) {
    this.eventService.uploadEventPicture(eventId, file).subscribe({
      next: () => {
        this.toastr.success('Event updated successfully!', 'Success');
        this.loadEvents();
        this.closeModal();
      },
      error: (error) => {
        this.toastr.error('Failed to upload event picture.', 'Error');
      }
    });
  }

  deleteEvent(eventId: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.eventService.deleteEvent(eventId).subscribe({
          next: () => {
            this.toastr.success('Event deleted successfully!', 'Success');
            this.loadEvents();
          },
          error: (error) => {
            this.toastr.error('Failed to delete event.', 'Error');
          }
        });
      }
    });
  }
}
