import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DeliverableService } from 'src/services/deliverable.service'; 
import { Deliverable } from 'src/app/models/deliverable.model';

@Component({
  selector: 'app-submission-detail',
  templateUrl: './submission-detail.component.html',
  styleUrls: ['./submission-detail.component.css'],
})
export class SubmissionDetailComponent implements OnInit {
  submission!: Deliverable;

  constructor(
    private route: ActivatedRoute,
    private deliverableService: DeliverableService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.deliverableService.getDeliverableById(id).subscribe({
      next: (data) => (this.submission = data),
      error: (error) => console.error('Error loading submission details:', error),
    });
  }
}