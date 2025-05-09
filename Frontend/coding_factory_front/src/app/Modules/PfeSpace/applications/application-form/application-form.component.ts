import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from '../../services/application.service';
import { ToastrService } from 'ngx-toastr';
import { Application } from '../../models/application.model';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserControllerService } from 'src/app/services/services/user-controller.service';

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
  userId: number | null = null;
  userFullName: string = '';
  userEmail: string = '';
  userFirstName: string = '';
  userLastName: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private toastr: ToastrService,
    private projectService: ProjectService,
    private jwtHelper: JwtHelperService,
    private userService: UserControllerService
  ) {
    this.applicationForm = this.fb.group({
      studentName: [{value: '', disabled: true}, [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      studentEmail: [{value: '', disabled: true}, [Validators.required, Validators.email]],
      cvFile: [null, Validators.required]
    });

    // Get user information from JWT token
    this.getUserInfoFromToken();
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

  // Get user information from JWT token
  private getUserInfoFromToken(): void {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const decodedToken = this.jwtHelper.decodeToken(token);
        this.userId = decodedToken.userId;

        // Get user details from the API
        if (this.userId) {
          this.userService.getUserById({ id: this.userId }).subscribe({
            next: (user) => {
              this.userFirstName = user.firstname || '';
              this.userLastName = user.lastname || '';
              this.userFullName = this.userFirstName + ' ' + this.userLastName;
              this.userEmail = user.email || '';

              // Update form with user information
              this.applicationForm.patchValue({
                studentName: this.userFullName,
                studentEmail: this.userEmail
              });
            },
            error: (error) => {
              console.error('Error fetching user details:', error);
              this.toastr.error('Failed to load user information');
            }
          });
        }
      } catch (error) {
        console.error('Error decoding token:', error);
      }
    } else {
      this.toastr.warning('You need to be logged in to submit an application');
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
    }
  }

  onSubmit(): void {
    console.log('Submit button clicked');
    console.log('Form valid:', this.applicationForm.valid);
    console.log('Form values:', this.applicationForm.getRawValue()); // Use getRawValue to get disabled fields
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

    if (!this.userId || !this.userFullName || !this.userEmail) {
      this.toastr.warning('User information is missing. Please log in again.');
      return;
    }

    this.isSubmitting = true;
    console.log('Submitting application...');

    const applicationData: any = {
      project: { id: this.projectId },
      studentName: this.userFullName,
      studentEmail: this.userEmail,
      status: 'PENDING',
      archived: false,
      userId: this.userId
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
