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
    this.router.navigate(['/back/participations', eventId]);
  }

  showFeedback(eventId: number) {
    this.router.navigate(['/back/feedbacks', eventId]);
  }



  deleteEvent(eventId: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'Do you really want to delete this event? This action cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.eventService.deleteEvent(eventId).subscribe({
          next: () => {
            this.toastr.success('Event deleted successfully!', 'Success');
            this.loadEvents(); // Refresh the list after deletion

            // Show success message with Swal
            Swal.fire('Deleted!', 'The event has been deleted.', 'success');
          },
          error: (error) => {
            this.toastr.error('Failed to delete event.', 'Error');
          }
        });
      }
    });
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
        console.log("updated event",eventData);
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

  onFileSelected(event: any) {
    this.selectedEventCover=event.target.files[0];
    console.log(this.selectedEventCover);
    if(this.selectedEventCover)
    {
      const reader=new FileReader();
      reader.onload=()=>{
        this.selectedPicture=reader.result as string;

      }
      reader.readAsDataURL(this.selectedEventCover);
    }

  }
   closeModal()
  {
    const modalElement = document.getElementById('updateModal');
    if (modalElement) {
      modalElement.classList.remove('show');
      modalElement.style.display = 'none';
      document.body.classList.remove('modal-open');
    }
  }
  uploadEventPicture(eventId: number, file: File) {
    this.eventService.uploadEventPicture(eventId, file).subscribe(() => {
      this.loadEvents();
      this.selectedEventCover = null; // Clear selected file
      this.closeModal();
    }, (error) => {
      this.toastr.error('Failed to upload event image.', 'Error');
    });
  }


}
