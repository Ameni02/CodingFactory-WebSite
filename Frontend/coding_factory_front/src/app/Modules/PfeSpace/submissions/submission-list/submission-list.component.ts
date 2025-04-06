import { Component, OnInit } from '@angular/core';
import { DeliverableService } from 'src/services/deliverable.service'; 
import { Deliverable } from 'src/app/models/deliverable.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-submission-list',
  templateUrl: './submission-list.component.html',
  styleUrls: ['./submission-list.component.css'],
})
export class SubmissionListComponent implements OnInit {
  submissions: Deliverable[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private deliverableService: DeliverableService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadSubmissions();
  }

  loadSubmissions(): void {
    this.isLoading = true;
    this.error = null;
    
    this.deliverableService.getAllDeliverables().subscribe({
      next: (data) => {
        console.log('Received submissions:', data);
        this.submissions = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading submissions:', error);
        this.error = 'Failed to load submissions. Please try again later.';
        this.isLoading = false;
        this.snackBar.open(this.error, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}