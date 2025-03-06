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
  descriptionFile: File | null = null; // Declare the description file property
  reportFile: File | null = null; // Declare the report file property

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
      descriptionFilePath: [null, Validators.required],
      reportFilePath: [null, Validators.required],
      submissionDate: [
        new Date().toISOString().split('T')[0],
        Validators.required,
      ],
    });
  }

  // Method to handle the form submission
  onSubmit(): void {
    if (this.submissionForm.invalid) {
      alert('Please fill out all required fields.');
      return; // Prevent submission if form is invalid
    }

    // Create FormData object
    const formData = new FormData();

    // Get form control values
    const projectId = this.submissionForm.get('projectId')?.value;
    const academicSupervisorId = this.submissionForm.get('academicSupervisorId')?.value;
    const title = this.submissionForm.get('title')?.value;
    const submissionDate = this.submissionForm.get('submissionDate')?.value || new Date().toISOString().split('T')[0];

    // Append form values to FormData
    formData.append('projectId', projectId);
    formData.append('academicSupervisorId', academicSupervisorId);
    formData.append('title', title);
    formData.append('submissionDate', submissionDate);

    // Only append files if they are selected
    if (this.descriptionFile) {
      console.log('Selected Description File:', this.descriptionFile);
      formData.append('descriptionFile', this.descriptionFile);
    } else {
      alert('Please select a description file before submitting.'); // File is missing
      return;
    }

    if (this.reportFile) {
      console.log('Selected Report File:', this.reportFile);
      formData.append('reportFile', this.reportFile);
    } else {
      alert('Please select a report file before submitting.'); // File is missing
      return;
    }

    // Call the service to add the deliverable
    this.deliverableService.addDeliverable(formData).subscribe(
      (response) => {
        console.log('Deliverable added successfully:', response);
        alert('Deliverable created successfully!');
        this.router.navigate(['/deliverables']);
      },
      (error) => {
        console.error('Error creating deliverable:', error);
        alert('Error creating deliverable. Please try again.');
      }
    );
  }

  handleDescriptionFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files) {
      this.descriptionFile = input.files[0]; // Get the first file
      this.submissionForm.get('descriptionFilePath')?.setValue(input.files[0].name); // Set the value in form
    }
  }

  handleReportFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files) {
      this.reportFile = input.files[0]; // Get the first file
      this.submissionForm.get('reportFilePath')?.setValue(input.files[0].name); // Set the value in form
    }
  }
}
