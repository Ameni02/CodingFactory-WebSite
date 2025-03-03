import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DeliverableService } from 'src/services/deliverable.service'; 
import { Deliverable } from 'src/app/models/deliverable.model'; 
import { Router } from '@angular/router';

@Component({
  selector: 'app-submission-form',
  templateUrl: './submission-form.component.html',
  styleUrls: ['./submission-form.component.css'],
})
export class SubmissionFormComponent implements OnInit {
  submissionForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private deliverableService: DeliverableService,
    private router: Router
  ) {
    this.submissionForm = this.fb.group({
      projectId: ['', Validators.required],
      academicSupervisorId: ['', Validators.required],
      title: ['', Validators.required],
      descriptionFilePath: ['', Validators.required],
      reportFilePath: ['', Validators.required],
      submissionDate: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.submissionForm.valid) {
      const newDeliverable: Deliverable = this.submissionForm.value;
      this.deliverableService.addDeliverable(newDeliverable).subscribe({
        next: (response) => {
          console.log('Deliverable submitted successfully!', response);
          this.router.navigate(['/submissions', response.id]);
        },
        error: (error) => {
          console.error('Error submitting deliverable:', error);
        },
      });
    }
  }
}