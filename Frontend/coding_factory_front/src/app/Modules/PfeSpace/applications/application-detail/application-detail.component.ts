import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from '../../services/application.service';
import { Application } from '../../models/application.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-application-detail',
  templateUrl: './application-detail.component.html',
  styleUrls: ['./application-detail.component.css']
})
export class ApplicationDetailComponent implements OnInit {
  application: Application | null = null;
  isLoading = false;
  isDownloadingCv = false;
  isDownloadingCoverLetter = false;
  analysisResult: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadApplication(+id);
    }
  }

  private loadApplication(id: number): void {
    this.isLoading = true;
    this.applicationService.getApplication(id).subscribe({
      next: (application: Application) => {
        this.application = application;

        // Check for CV analysis results in either format
        if (application.cvAnalysisResult) {
          // Using the old format
          this.analysisResult = application.cvAnalysisResult;
        } else if (application.cvAnalysisScore !== undefined && application.cvAnalysisFeedback) {
          // Using the new format - create a compatible structure
          this.analysisResult = {
            score: application.cvAnalysisScore,
            feedback: application.cvAnalysisFeedback,
            detailedScores: application.detailedScores || {},
            applicationStatus: application.status
          };
        }

        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.toastr.error('Failed to load application details');
        this.router.navigate(['/pfe-space/applications']);
      }
    });
  }

  getDetailedScores(): { label: string; value: number; max: number }[] {
    if (!this.analysisResult) return [];

    return [
      { label: 'Education', value: this.analysisResult.detailedScores.education, max: 25 },
      { label: 'Experience', value: this.analysisResult.detailedScores.experience, max: 30 },
      { label: 'Skills', value: this.analysisResult.detailedScores.skills, max: 30 },
      { label: 'Project Relevance', value: this.analysisResult.detailedScores.projectRelevance, max: 15 },
      { label: 'Field Match', value: this.analysisResult.detailedScores.fieldMatch, max: 10 },
      { label: 'Title Match', value: this.analysisResult.detailedScores.titleMatch, max: 10 }
    ];
  }

  goBack(): void {
    this.router.navigate(['/offers']);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'ACCEPTED':
        return 'text-success';
      case 'PENDING':
        return 'text-warning';
      case 'REJECTED':
        return 'text-danger';
      default:
        return 'text-secondary';
    }
  }

  getScoreClass(score: number): string {
    if (score >= 60) return 'text-success';
    if (score >= 50) return 'text-warning';
    return 'text-danger';
  }

  downloadFile(fileType: 'cv' | 'coverLetter'): void {
    if (!this.application?.id) {
      this.toastr.warning('Application details not available');
      return;
    }

    const filePath = fileType === 'cv' ? this.application.cvFilePath : this.application.coverLetterFilePath;
    console.log(`Downloading ${fileType} for application ID:`, this.application.id);
    if (filePath) {
      console.log(`${fileType} file path:`, filePath);
    } else {
      console.log(`No ${fileType} file path available, will try to download anyway`);
    }

    if (fileType === 'cv') {
      this.isDownloadingCv = true;
    } else {
      this.isDownloadingCoverLetter = true;
    }

    this.applicationService.downloadFile(this.application.id, fileType).subscribe({
      next: (blob: Blob) => {
        console.log('Download successful, blob size:', blob.size);
        console.log('Content type:', blob.type);

        if (blob.size === 0) {
          if (fileType === 'cv') {
            this.isDownloadingCv = false;
          } else {
            this.isDownloadingCoverLetter = false;
          }
          this.toastr.error('Empty file received');
          return;
        }

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;

        // Determine file extension based on content type
        let fileExtension = '.pdf';
        if (blob.type === 'text/plain') {
          fileExtension = '.txt';
        }

        a.download = `${this.application?.studentName ?? 'student'}_${fileType}${fileExtension}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        if (fileType === 'cv') {
          this.isDownloadingCv = false;
        } else {
          this.isDownloadingCoverLetter = false;
        }

        this.toastr.success(`${fileType === 'cv' ? 'CV' : 'Cover Letter'} downloaded successfully`);
      },
      error: (error: any) => {
        console.error('Error downloading file:', error);
        if (fileType === 'cv') {
          this.isDownloadingCv = false;
        } else {
          this.isDownloadingCoverLetter = false;
        }
        this.toastr.error(`Failed to download ${fileType === 'cv' ? 'CV' : 'Cover Letter'}: ${error.message || 'Unknown error'}`);
      }
    });
  }

  acceptApplication(): void {
    if (!this.application?.id) return;

    this.applicationService.acceptApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application accepted successfully');
        if (this.application?.id !== undefined) {
          this.loadApplication(this.application.id);
        }
      },
      error: () => {
        this.toastr.error('Failed to accept application');
      }
    });
  }

  rejectApplication(): void {
    if (!this.application?.id) return;

    this.applicationService.rejectApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application rejected successfully');
        if (this.application?.id !== undefined) {
          this.loadApplication(this.application.id);
        }
      },
      error: () => {
        this.toastr.error('Failed to reject application');
      }
    });
  }
}
