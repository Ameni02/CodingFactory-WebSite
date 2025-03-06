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
  submission: Deliverable | undefined;

  constructor(
    private route: ActivatedRoute,
    private deliverableService: DeliverableService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Fetched ID:', id);  // Log the ID to confirm it's being fetched

    if (id) {
      this.loadSubmissionDetail(+id);
    }
  }

  loadSubmissionDetail(id: number): void {
    this.deliverableService.getDeliverableById(id).subscribe({
      next: (data) => {
        console.log('Fetched Data:', data);  // Log the fetched data to verify it's correct
        this.submission = data;
      },
      error: (error) => {
        console.error('Error loading submission details:', error);
      },
    });
  }
}
