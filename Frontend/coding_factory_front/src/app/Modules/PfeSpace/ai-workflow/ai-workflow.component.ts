import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AiWorkflowService } from '../services/ai-workflow.service';
import { TestService } from '../services/test.service';
import { Project } from '../models/project.model';
import { ProjectDTO, ProjectMatch } from '../models/ai-workflow.model';
import { ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserControllerService } from 'src/app/services/services/user-controller.service';

@Component({
  selector: 'app-ai-workflow',
  templateUrl: './ai-workflow.component.html',
  styleUrls: ['./ai-workflow.component.css']
})
export class AiWorkflowComponent implements OnInit {
  uploadForm: FormGroup;
  applicationForm: FormGroup;
  isLoading = false;
  isMatching = false;
  isGenerating = false;

  cvFile: File | null = null;
  cvText: string = '';
  matchedProjects: ProjectMatch[] = [];
  keySkills: string[] = [];
  selectedProject: Project | null = null;
  selectedProjectDTO: ProjectDTO | null = null;
  generatedCoverLetter: string = '';
  generatedBy: string = '';

  userId: number | null = null;
  userFullName: string = '';
  userEmail: string = '';
  userFirstName: string = '';
  userLastName: string = '';

  currentStep = 1; // 1: Upload CV, 2: Select Project, 3: Generate Cover Letter, 4: Review & Submit

  constructor(
    private fb: FormBuilder,
    private aiWorkflowService: AiWorkflowService,
    private testService: TestService,
    private toastr: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private jwtHelper: JwtHelperService,
    private userService: UserControllerService
  ) {
    this.uploadForm = this.fb.group({
      cvFile: [null, Validators.required]
    });

    this.applicationForm = this.fb.group({
      studentName: [{value: '', disabled: true}, [Validators.required, Validators.minLength(2)]],
      studentEmail: [{value: '', disabled: true}, [Validators.required, Validators.email]]
    });

    // Get user information from JWT token
    this.getUserInfoFromToken();
  }

  ngOnInit(): void {
    // Check if a project ID was passed in the route
    this.route.queryParams.subscribe(params => {
      const projectId = params['projectId'];
      if (projectId) {
        // Pre-select this project after matching
        this.preSelectedProjectId = Number(projectId);
      }
    });

    // Test the connection to the backend silently
    this.testService.ping().subscribe({
      next: (response) => {
        console.log('Ping successful:', response);
        // No toast notification for successful connection
      },
      error: (error) => {
        console.error('Ping failed:', error);
        this.toastr.error('Failed to connect to backend');
      }
    });
  }

  preSelectedProjectId: number | null = null;

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
      this.toastr.warning('You need to be logged in to use this feature');
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
    }
  }

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      this.cvFile = event.target.files[0];

      // Validate file type
      if (this.cvFile && this.cvFile.type !== 'application/pdf') {
        this.toastr.error('Please upload a PDF file');
        this.cvFile = null;
        this.uploadForm.get('cvFile')?.setValue(null);
        return;
      }
    }
  }

  onUploadCV(): void {
    if (this.uploadForm.invalid || !this.cvFile) {
      this.toastr.error('Please select a valid CV file');
      return;
    }

    this.isMatching = true;

    this.aiWorkflowService.matchProjects(this.cvFile).subscribe({
      next: (response) => {
        this.matchedProjects = response.matchedProjects;
        this.keySkills = response.keySkills;
        this.cvText = response.cvText;

        if (this.matchedProjects.length === 0) {
          this.toastr.warning('No matching projects found. Try uploading a different CV or check back later for new projects.');
        } else {
          this.toastr.success(`Found ${this.matchedProjects.length} matching projects!`);
          this.currentStep = 2;

          // If there was a pre-selected project, select it now
          if (this.preSelectedProjectId) {
            const preSelectedMatch = this.matchedProjects.find(
              match => match.project.id === this.preSelectedProjectId
            );
            if (preSelectedMatch) {
              this.selectProject(preSelectedMatch.project);
            }
          }
        }

        this.isMatching = false;
      },
      error: (error) => {
        console.error('Error matching projects:', error);
        this.toastr.error('Failed to match projects. Please try again.');
        this.isMatching = false;
      }
    });
  }

  selectProject(project: Project): void {
    this.selectedProject = project;
    this.currentStep = 3;
  }

  generateCoverLetter(): void {
    if (!this.selectedProject) {
      this.toastr.error('Please select a project');
      return;
    }

    if (!this.userId || !this.userFullName || !this.userEmail) {
      this.toastr.warning('User information is missing. Please log in again.');
      return;
    }

    this.isGenerating = true;

    const requestData = {
      projectId: this.selectedProject!.id,
      cvText: this.cvText || '',
      studentName: this.userFullName,
      studentEmail: this.userEmail,
      userId: this.userId
    };

    console.log('Generating cover letter with:', {
      projectId: requestData.projectId,
      cvTextLength: requestData.cvText.length,
      studentName: requestData.studentName,
      studentEmail: requestData.studentEmail
    });

    // First test with the echo endpoint
    this.testService.echo(requestData).subscribe({
      next: (echoResponse) => {
        console.log('Echo successful:', echoResponse);

        // Now try the actual cover letter generation
        this.aiWorkflowService.generateCoverLetter(
          requestData.projectId,
          requestData.cvText,
          requestData.studentName,
          requestData.studentEmail
        ).subscribe({
          next: (response) => {
            this.generatedCoverLetter = response.coverLetter;
            this.selectedProjectDTO = response.project;
            this.generatedBy = response.generatedBy || 'AI Service';
            this.toastr.success('Cover letter generated successfully!');
            this.currentStep = 4;
            this.isGenerating = false;
          },
          error: (error) => {
            console.error('Error generating cover letter:', error);
            this.toastr.error('Failed to generate cover letter. Please try again.');
            this.isGenerating = false;
          }
        });
      },
      error: (error) => {
        console.error('Echo failed:', error);
        this.toastr.error('Failed to connect to backend. Please try again.');
        this.isGenerating = false;
      }
    });
  }

  downloadCoverLetter(): void {
    if (!this.generatedCoverLetter) {
      this.toastr.error('No cover letter to download');
      return;
    }

    const blob = new Blob([this.generatedCoverLetter], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    const projectTitle = this.selectedProjectDTO?.title || this.selectedProject?.title || 'Project';
    a.download = `Cover_Letter_${projectTitle.replace(/\s+/g, '_')}.txt`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }

  resetWorkflow(): void {
    this.currentStep = 1;
    this.cvFile = null;
    this.cvText = '';
    this.matchedProjects = [];
    this.keySkills = [];
    this.selectedProject = null;
    this.selectedProjectDTO = null;
    this.generatedCoverLetter = '';
    this.generatedBy = '';
    this.uploadForm.reset();
    this.applicationForm.reset();
  }

  goBack(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }
}
