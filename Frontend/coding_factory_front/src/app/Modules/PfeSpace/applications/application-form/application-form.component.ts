import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { ToastrService } from 'ngx-toastr';
import { Application } from 'src/app/models/application.model';

@Component({
  selector: 'app-application-form',
  templateUrl: './application-form.component.html',
  styleUrls: ['./application-form.component.css']
})
export class ApplicationFormComponent implements OnInit {
  applicationForm: FormGroup;
  projectId!: number;
  cvFile: File | null = null;
  coverLetterFile: File | null = null;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private toastr: ToastrService
  ) {
    this.applicationForm = this.fb.group({
      studentName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      studentEmail: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const projectId = params['projectId'];
      if (projectId) {
        this.projectId = +projectId;
        console.log('Project ID:', this.projectId); // Debug log
      } else {
        console.error('No project ID found in query parameters');
        this.toastr.error('No project selected');
        this.router.navigate(['/pfe-space/offers']);
      }
    });
  }

  onFileSelected(event: Event, type: 'cv' | 'coverLetter'): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (type === 'cv') {
        this.cvFile = file;
      } else {
        this.coverLetterFile = file;
      }
    }
  }

  onSubmit(): void {
    if (this.applicationForm.invalid) {
      this.toastr.warning('Please fill in all required fields correctly');
      this.applicationForm.markAllAsTouched();
      return;
    }
  
    if (!this.cvFile || !this.coverLetterFile) {
      this.toastr.warning('Please upload both CV and Cover Letter');
      return;
    }
  
    this.isSubmitting = true;
  
    const applicationData: Application = {
      project: { id: this.projectId },
      studentName: this.applicationForm.value.studentName,
      studentEmail: this.applicationForm.value.studentEmail,
      cvFilePath: '', // Will be set by backend
      coverLetterFilePath: '', // Will be set by backend
      status: 'PENDING',
      archived: false
    };
  
    this.applicationService.addApplication(
      this.projectId,
      applicationData,
      this.cvFile,
      this.coverLetterFile
    ).subscribe({
      next: (response) => {
        this.toastr.success('Application submitted successfully!');
        this.router.navigate(['/applications', response.id]);
      },
      error: (error) => {
        this.isSubmitting = false;
        // Error is already handled by the service
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }
  isValidPdf(file: File): boolean {
    return file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
  }
  onCancel(): void {
    if (this.projectId) {
      this.router.navigate(['/offers', this.projectId]);
    } else {
      this.router.navigate(['/offers']);
    }
  }
}