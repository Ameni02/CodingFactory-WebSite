import {Component, OnInit} from '@angular/core';
import {FeedbackService} from "../../../services/feedback.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.scss']
})
export class FeedbacksComponent implements OnInit{
  feedbacks: any[] = [];
  eventId:number=0;

  constructor(private router:Router,private _router:ActivatedRoute,private feedbackService: FeedbackService, private toastr: ToastrService) {}

  ngOnInit(): void {
    this.eventId=this._router.snapshot.params['id'];
    this.loadFeedbacks();
  }

  // Fetch all feedbacks
  loadFeedbacks() {
    this.feedbackService.getFeedbackForEvent(this.eventId).subscribe({
      next: (data: any[]) => {
        this.feedbacks = data;
        console.log(data)
      },
      error: (error: any) => {
        console.error('Error loading feedbacks:', error);
      }
    });
  }


  deleteFeedback(feedbackId: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'Do you really want to delete this feedback? This action cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.feedbackService.deleteFeedback(feedbackId).subscribe({
          next: () => {
            this.toastr.success('Feedback deleted successfully!', 'Success');


            this.feedbacks = this.feedbacks.filter(f => f.id !== feedbackId);


            Swal.fire('Deleted!', 'The feedback has been deleted.', 'success');
          },
          error: (error) => {
            this.toastr.error('Failed to delete feedback.', 'Error');
            console.error('Error deleting feedback:', error);
          }
        });
      }
    });
  }


  goBack() {
    this.router.navigate(['/back']);
  }
}
