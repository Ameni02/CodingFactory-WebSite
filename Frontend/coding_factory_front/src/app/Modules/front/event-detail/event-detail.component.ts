import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ParicipationService} from "../../../services/paricipation.service";
import {FeedbackService} from "../../../services/feedback.service";
import {EventRatingService} from "../../../services/event-rating.service";

@Component({
  selector: 'app-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EventDetailComponent implements OnInit {
  event: any;
  userId: number = 1;
  isParticipating: boolean = false;
  feedbacks:any[]=[];
  feedback:any={};
  averageRating: number = 0;


  constructor(
    private eventRatingService: EventRatingService,
    private feedbackService: FeedbackService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private participationService: ParicipationService
  ) {
    // Get event data from router state
    const navigation = window.history.state;
    this.event = navigation.eventData ? navigation.eventData : null;

    console.log('Event data received in detail component:', this.event);
  }

  ngOnInit(): void {
    // If event data wasn't passed through router state, try to get it from route params
    if (!this.event) {
      const eventId = this.route.snapshot.paramMap.get('id');
      if (eventId) {
        // TODO: Add a service method to fetch event by ID
        this.toastr.warning('Event data not available, please go back to events list');
        console.log('Need to fetch event with ID:', eventId);
      }
    }

    if (this.event) {
      this.loadAverageRating();
      this.participationService.participation$.subscribe(status => {
        this.isParticipating = status;
      });
      this.checkUserParticipation(this.event.id);
      this.loadFeedbackForAnEvent();
    }
  }

  participateInEvent() {
    if (!this.event || !this.event.id) {
      this.toastr.error("Event details are missing.", "Error");
      return;
    }

    // Check if user is already participating
    this.participationService.isUserParticipating(this.event.id, this.userId).subscribe({
      next: (isParticipating) => {
        if (isParticipating) {
          this.toastr.warning("You are already registered for this event!", "Warning");
          return;
        }

        // If not participating, register
        this.registerForEvent();
      },
      error: (error) => {
        console.error("Error checking participation status:", error);
        // If we can't check, try to register anyway
        this.registerForEvent();
      }
    });
  }

  private registerForEvent() {
    // Make sure we have valid IDs
    if (!this.event?.id || !this.userId) {
      this.toastr.error("Missing event or user information", "Error");
      return;
    }

    const participationRequest = {
      eventId: this.event.id,
      userId: this.userId
    };

    console.log('Sending participation request:', participationRequest);

    this.participationService.registerParticipation(participationRequest).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.toastr.success("You have successfully registered for this event!", "Success");
        this.isParticipating = true;
      },
      error: (error) => {
        console.error("Error registering participation:", error);

        // Handle different error cases
        if (error.error && error.error.message) {
          this.toastr.error(error.error.message, "Error");
        } else if (error.status === 400) {
          this.toastr.error("Invalid request. Please try again.", "Error");
        } else if (error.status === 404) {
          this.toastr.error("Event not found.", "Error");
        } else if (error.status === 500) {
          this.toastr.error("Server error. Please try again later.", "Error");
        } else {
          this.toastr.error("Failed to register for the event.", "Error");
        }
      }
    });
  }
  checkUserParticipation(eventId: number) {
    this.participationService.isUserParticipating(eventId, this.userId).subscribe({
      next: (isParticipating) => {
        this.isParticipating = isParticipating;
      },
      error: (error) => {
        console.error("Error checking participation status:", error);
      }
    });
  }
  loadFeedbackForAnEvent()
  {
    this.feedbackService.getFeedbackForEvent(this.event.id).subscribe({
      next: (feedbacks) => {
        this.feedbacks = feedbacks;
        console.log("Feedback loaded:", this.feedbacks);
      },
      error: (error) => {
        this.toastr.error("Failed to load feedback.", "Error");
        console.error("Error loading feedback:", error);
      }
    });

  }

  submitFeedback() {
    if ( !this.feedback.comment) {
      this.toastr.warning("Please provide both rating and comment.", "Validation Error");
      return;
    }

    const feedbackRequest = {
      eventId: this.event.id,
      userId: this.userId,

      comment: this.feedback.comment
    };

    this.feedbackService.submitFeedback(feedbackRequest).subscribe({
      next: (response) => {
        this.toastr.success("Feedback submitted successfully!", "Success");
        this.feedbacks.unshift(response);
        this.feedback = {  };
        (document.getElementById('feedbackModal') as any).modal('hide');
      },
      error: () => {
        this.toastr.error("Failed to submit feedback.", "Error");
      }
    });
  }


  private loadAverageRating() {
    if (!this.event || !this.event.id) {
      console.error('Cannot load average rating: Event ID is missing');
      return;
    }

    this.eventRatingService.getAverageRating(this.event.id).subscribe({
      next: (response) => {
        this.averageRating = response;
        console.log("Average rating loaded:", response);
      },
      error: (error) => {
        console.error("Error loading average rating:", error);
        // Set default rating to 0 if there's an error
        this.averageRating = 0;
      }
    });
  }
  onRatingChange(selectedRating: number) {
    console.log('Rating changed to:', selectedRating);
    this.submitRating(selectedRating);  // Submit the rating after the change
  }

  submitRating(selectedRating:number) {
    if (selectedRating <= 0 || selectedRating > 5) {
      this.toastr.warning("Please select a valid rating between 1 and 5.", "Validation Error");
      return;
    }

    if (!this.event?.id || !this.userId) {
      this.toastr.error("Missing event or user information", "Error");
      return;
    }

    console.log('Submitting rating:', {
      eventId: this.event.id,
      rating: selectedRating,
      userId: this.userId
    });

    // Convert all values to ensure they're numbers
    const eventId = Number(this.event.id);
    const rating = Number(selectedRating);
    const userId = Number(this.userId);

    console.log('Converted values:', { eventId, rating, userId });

    // Try the direct addRating method with the text response type
    this.eventRatingService.addRating(eventId, rating, userId).subscribe({
      next: () => {
        this.toastr.success("Rating successfully!", "Success");
        // After submitting, reload the average rating
        this.loadAverageRating();
      },
      error: (error) => {
        console.error("Error submitting rating:", error);

        // Check if the error message indicates the user has already rated
        if (error.error && error.error.includes && error.error.includes('already rated')) {
          // Silently try to update the rating instead without showing a warning
          this.eventRatingService.updateRating(eventId, rating, userId).subscribe({
            next: () => {
              this.toastr.success("Rating successfully!", "Success");
              this.loadAverageRating();
            },
            error: (updateError) => {
              console.error("Error updating rating:", updateError);
              this.toastr.error("Failed to update rating.", "Error");
            }
          });
        } else {
          this.toastr.error("Failed to submit rating.", "Error");
        }
      }
    });
  }

  getEventImage(): string {
    if (this.event && this.event.imageBase64) {
      return 'data:image/jpg;base64,' + this.event.imageBase64;
    } else if (this.event && this.event.image) {
      // If we have a raw image but not base64
      try {
        // Convert the raw image to base64
        let binary = '';
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
}
