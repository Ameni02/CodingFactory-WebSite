import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { ToastrService } from 'ngx-toastr';
import { Application } from 'src/app/models/application.model';
import { Project } from 'src/app/models/project.model';
import { ProjectService } from 'src/services/project.service';

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
  project!: Project;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private toastr: ToastrService,
    private projectService: ProjectService
  ) {
    this.applicationForm = this.fb.group({
      studentName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      studentEmail: ['', [Validators.required, Validators.email]],
      cvFile: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const projectId = params['projectId'];
      if (projectId) {
        this.projectId = +projectId;
        this.loadProjectDetails();
      } else {
        this.toastr.error('No project selected');
        this.router.navigate(['/pfe-space/offers']);
        return;
      }
    });
  }

  private loadProjectDetails(): void {
    this.isLoading = true;
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.toastr.error('Failed to load project details');
        this.router.navigate(['/pfe-space/offers']);
      }
    });
  }

  onFileSelected(event: any, type: 'cv' | 'coverLetter'): void {
    const fileInput = event.target;
    if (!fileInput.files || fileInput.files.length === 0) return;

    const file = fileInput.files[0];
    if (!file) return;

    if (type === 'cv') {
      if (this.isValidPdf(file)) {
        this.cvFile = file;
        this.applicationForm.patchValue({ cvFile: file });
      } else {
        this.toastr.error('Please upload a valid PDF file for CV');
        fileInput.value = '';
      }
    } else {
      if (this.isValidPdf(file)) {
        this.coverLetterFile = file;
      } else {
        this.toastr.error('Please upload a valid PDF file for Cover Letter');
        fileInput.value = '';
      }
    }
  }

  onSubmit(): void {
    console.log('Submit button clicked');
    console.log('Form valid:', this.applicationForm.valid);
    console.log('Form values:', this.applicationForm.value);
    console.log('CV file:', this.cvFile);
    console.log('Cover letter file:', this.coverLetterFile);

    if (this.applicationForm.invalid) {
      console.log('Form validation errors:', this.applicationForm.errors);
      this.toastr.warning('Please fill in all required fields correctly');
      this.applicationForm.markAllAsTouched();
      return;
    }

    if (!this.cvFile || !this.coverLetterFile) {
      this.toastr.warning('Please upload both CV and Cover Letter');
      return;
    }

    this.isSubmitting = true;
    console.log('Submitting application...');

    const applicationData: any = {
      project: { id: this.projectId },
      studentName: this.applicationForm.value.studentName,
      studentEmail: this.applicationForm.value.studentEmail,
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
        console.log('Application submitted successfully:', response);
        this.toastr.success('Application submitted successfully!');
        this.router.navigate(['/pfe-space/applications', response.id]);
      },
      error: (error) => {
        console.error('Error submitting application:', error);
        this.isSubmitting = false;
        this.toastr.error('Failed to submit application: ' + (error.message || 'Unknown error'));
      }
    });
  }

  onCancel(): void {
    if (this.projectId) {
      this.router.navigate(['/pfe-space/offers', this.projectId]);
    } else {
      this.router.navigate(['/pfe-space/offers']);
    }
  }

  isValidPdf(file: File): boolean {
    return file.type === 'application/pdf';
  }
}
