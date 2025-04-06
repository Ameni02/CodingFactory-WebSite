import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DeliverableService } from 'src/services/deliverable.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-submission-form',
  templateUrl: './submission-form.component.html',
  styleUrls: ['./submission-form.component.css'],
})
export class SubmissionFormComponent implements OnInit {
  submissionForm!: FormGroup;
  descriptionFile: File | null = null;
  reportFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private deliverableService: DeliverableService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.submissionForm = this.fb.group({
      projectId: [null, Validators.required],
      academicSupervisorId: [null, Validators.required],
      title: ['', Validators.required],
      submitterEmail: ['', [Validators.required, Validators.email]],
      descriptionFilePath: [null, Validators.required],
      reportFilePath: [null, Validators.required],
      submissionDate: [new Date().toISOString().split('T')[0], Validators.required],
    });
  }

  onDescriptionFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.descriptionFile = input.files[0];
      this.submissionForm.patchValue({
        descriptionFilePath: input.files[0].name
      });
    } else {
      this.descriptionFile = null;
      this.submissionForm.patchValue({
        descriptionFilePath: null
      });
    }
  }
  
  onReportFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.reportFile = input.files[0];
      this.submissionForm.patchValue({
        reportFilePath: input.files[0].name
      });
    } else {
      this.reportFile = null;
      this.submissionForm.patchValue({
        reportFilePath: null
      });
    }
  }

  onSubmit(): void {
    if (this.submissionForm.invalid || !this.descriptionFile || !this.reportFile) {
      this.snackBar.open('Please fill all required fields and select files', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }
  
    const formData = new FormData();
    
    // Add non-file fields
    formData.append('projectId', this.submissionForm.get('projectId')?.value.toString());
    formData.append('academicSupervisorId', this.submissionForm.get('academicSupervisorId')?.value.toString());
    formData.append('title', this.submissionForm.get('title')?.value);
    formData.append('submitterEmail', this.submissionForm.get('submitterEmail')?.value);
    formData.append('submissionDate', this.submissionForm.get('submissionDate')?.value);
  
    // Add files - we've already checked they're not null
    formData.append('descriptionFile', this.descriptionFile as Blob);
    formData.append('reportFile', this.reportFile as Blob);

    console.log('Submitting form data:', {
      projectId: formData.get('projectId'),
      academicSupervisorId: formData.get('academicSupervisorId'),
      title: formData.get('title'),
      submitterEmail: formData.get('submitterEmail'),
      submissionDate: formData.get('submissionDate'),
      descriptionFile: this.descriptionFile?.name,
      reportFile: this.reportFile?.name
    });
  
    this.deliverableService.submitDeliverable(formData).subscribe({
      next: (response) => {
        console.log('Submission successful:', response);
        this.snackBar.open('Submission successful!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        // Wait a bit before navigating to ensure the backend has processed the submission
        setTimeout(() => {
          this.router.navigate(['/submissions']);
        }, 1000);
      },
      error: (error) => {
        console.error('Submission error:', error);
        let errorMessage = 'Failed to submit deliverable';
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.error.message) {
            errorMessage = error.error.message;
          }
        }
        this.snackBar.open(`Error: ${errorMessage}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
