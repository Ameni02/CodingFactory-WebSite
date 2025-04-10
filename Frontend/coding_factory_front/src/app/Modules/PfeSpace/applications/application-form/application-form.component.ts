import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { CvAnalysisService } from 'src/services/cv-analysis.service';
import { ToastrService } from 'ngx-toastr';
import { Application } from 'src/app/models/application.model';
import { Project } from 'src/app/models/project.model';
import { ProjectService } from 'src/services/project.service';

interface CvAnalysisResult {
  isAdaptable: boolean;
  score: number;
  feedback: string;
  detailedScores: {
    education: number;
    experience: number;
    skills: number;
    projectRelevance: number;
    fieldMatch: number;
    titleMatch: number;
  };
  applicationStatus: string;
}

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
  isAnalyzing = false;
  analysisResult: CvAnalysisResult | null = null;
  project!: Project;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private cvAnalysisService: CvAnalysisService,
    private toastr: ToastrService,
    private projectService: ProjectService
  ) {
    this.applicationForm = this.fb.group({
      studentName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      studentEmail: ['', [Validators.required, Validators.email]],
      motivation: ['', Validators.required],
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
        this.analyzeCv();
      } else {
        this.toastr.error('Please upload a valid PDF file for CV');
        fileInput.value = ''; // Reset the file input
      }
    } else {
      if (this.isValidPdf(file)) {
        this.coverLetterFile = file;
      } else {
        this.toastr.error('Please upload a valid PDF file for Cover Letter');
        fileInput.value = ''; // Reset the file input
      }
    }
  }

  analyzeCv(): void {
    if (!this.cvFile || !this.projectId) return;

    this.isAnalyzing = true;
    this.cvAnalysisService.analyzeCv(this.projectId, this.cvFile).subscribe({
      next: (result: { isAdaptable: boolean; score: number; feedback: string }) => {
        this.analysisResult = {
          isAdaptable: result.isAdaptable,
          score: result.score,
          feedback: result.feedback,
          detailedScores: {
            education: 0,
            experience: 0,
            skills: 0,
            projectRelevance: 0,
            fieldMatch: 0,
            titleMatch: 0
          },
          applicationStatus: result.isAdaptable ? 'ACCEPTED' : 'REJECTED'
        };
        this.isAnalyzing = false;
        
        // Show appropriate message based on score
        if (result.score >= 60) {
          this.toastr.success(`CV Analysis: ${result.feedback} (Score: ${result.score})`);
        } else if (result.score >= 50) {
          this.toastr.info(`CV Analysis: ${result.feedback} (Score: ${result.score})`);
        } else {
          this.toastr.warning(`CV Analysis: ${result.feedback} (Score: ${result.score})`);
        }
      },
      error: (error) => {
        this.isAnalyzing = false;
        this.toastr.error('Failed to analyze CV');
        console.error('CV analysis error:', error);
      }
    });
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

    if (!this.analysisResult) {
      this.toastr.warning('Please wait for CV analysis to complete');
      return;
    }

    this.isSubmitting = true;

    const applicationData: Application = {
      project: { id: this.projectId },
      studentName: this.applicationForm.value.studentName,
      studentEmail: this.applicationForm.value.studentEmail,
      cvFilePath: '',
      coverLetterFilePath: '',
      status: this.analysisResult.applicationStatus as "PENDING" | "ACCEPTED" | "REJECTED",
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
        this.router.navigate(['/pfe-space/applications', response.id]);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.toastr.error('Failed to submit application');
      },
      complete: () => {
        this.isSubmitting = false;
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