import { Component, OnInit } from '@angular/core';
import { DeliverableService } from 'src/services/deliverable.service'; 
import { Deliverable } from 'src/app/models/deliverable.model';

@Component({
  selector: 'app-submission-list',
  templateUrl: './submission-list.component.html',
  styleUrls: ['./submission-list.component.css'],
})
export class SubmissionListComponent implements OnInit {
  submissions: Deliverable[] = [];

  constructor(private deliverableService: DeliverableService) {}

  ngOnInit(): void {
    this.loadSubmissions();
  }

  loadSubmissions(): void {
    this.deliverableService.getDeliverables().subscribe({
      next: (data) => (this.submissions = data),
      error: (error) => console.error('Error loading submissions:', error),
    });
  }
}