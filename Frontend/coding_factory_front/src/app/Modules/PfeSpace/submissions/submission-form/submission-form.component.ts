import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DeliverableService } from 'src/services/deliverable.service';
import { Router } from '@angular/router';

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
    private router: Router
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
      alert('Please fill all required fields and select files');
      return;
    }
  
    const formData = new FormData();
    
    // Add non-file fields
    formData.append('projectId', this.submissionForm.get('projectId')?.value.toString());
    formData.append('academicSupervisorId', this.submissionForm.get('academicSupervisorId')?.value.toString());
    formData.append('title', this.submissionForm.get('title')?.value);
    formData.append('submitterEmail', this.submissionForm.get('submitterEmail')?.value);
  
    // Add files - we've already checked they're not null
    formData.append('descriptionFile', this.descriptionFile as Blob);
    formData.append('reportFile', this.reportFile as Blob);
  
    this.deliverableService.submitDeliverable(formData).subscribe({
      next: (response) => {
        console.log('Submission successful:', response);
        this.router.navigate(['/deliverables']);
      },
      error: (error) => {
        console.error('Submission error:', error);
        alert(`Error: ${error.error?.message || 'Failed to submit deliverable'}`);
      }
    });
  }}
