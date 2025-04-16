import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {ToastrService} from "ngx-toastr";
import {finalize} from "rxjs";

@Component({
  selector: 'app-add-event-modal',
  templateUrl: './add-event-modal.component.html',
  styleUrls: ['./add-event-modal.component.scss']
})
export class AddEventModalComponent implements OnInit{
  event: any={};
  selectedEventCover:any;
  selectedPicture: string | undefined;
  @ViewChild('modalCloseButton') modalCloseButton!: ElementRef;
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
  constructor(private eventService: EventService,private toastr:ToastrService) {
  }
  ngOnInit(): void {



      this.resetEvent();

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
          this.toastr.success("Event created successfully!", "Success");

          if (this.selectedEventCover) {
            // ✅ Upload image before updating UI
            this.eventService.uploadEventPicture(response.id, this.selectedEventCover)
              .pipe(finalize(() => this.finishEventCreation())) // ✅ Call refresh after image upload
              .subscribe({
                next: () => {
                  this.toastr.success("Image uploaded successfully!", "Success");
                },
                error: (uploadError) => {
                  this.toastr.error("Failed to upload image.", "Error");
                  console.error("Error uploading image:", uploadError);
                }
              });
          } else {
            this.finishEventCreation();
          }
        },
        error: (error) => {
          this.toastr.error("Failed to create event.", "Error");
          console.error("Error saving event:", error);
        }
      });
    }
  }


  private resetEvent() {
    this.event = {
      title: '',
      description: '',
      date: '',
      location: '',
      category: '',
      maxParticipants: 0,
      price: 0.0,
      image: null

    };
  }

  private closeModal() {
    if (this.modalCloseButton) {
      this.modalCloseButton.nativeElement.click();
    }
  }

  private finishEventCreation() {
    this.eventService.refreshEvents();
    this.closeModal();
    this.resetEvent();
  }
}
