import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApplicationService } from 'src/services/application.service'; 
import { Application } from 'src/app/models/application.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-application-form',
  templateUrl: './application-form.component.html',
  styleUrls: ['./application-form.component.css'],
})
export class ApplicationFormComponent implements OnInit {
  applicationForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private applicationService: ApplicationService,
    private router: Router
  ) {
    this.applicationForm = this.fb.group({
      projectId: ['', Validators.required],
      studentName: ['', Validators.required],
      studentEmail: ['', [Validators.required, Validators.email]],
      cvFilePath: ['', Validators.required],
      coverLetterFilePath: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.applicationForm.valid) {
      const newApplication: Application = this.applicationForm.value;
      this.applicationService.addApplication(newApplication).subscribe({
        next: (response) => {
          console.log('Application submitted successfully!', response);
          this.router.navigate(['/applications', response.id]);
        },
        error: (error) => {
          console.error('Error submitting application:', error);
        },
      });
    }
  }
}