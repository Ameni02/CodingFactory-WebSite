import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { Application } from 'src/app/models/application.model';
import { ToastrService } from 'ngx-toastr';

interface CvAnalysisResult {
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
}

@Component({
  selector: 'app-application-detail',
  templateUrl: './application-detail.component.html',
  styleUrls: ['./application-detail.component.css']
})
export class ApplicationDetailComponent implements OnInit {
  application: Application | null = null;
  isLoading = false;
  analysisResult: CvAnalysisResult | null = null;

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
        if (application.cvAnalysisResult) {
          this.analysisResult = application.cvAnalysisResult;
        }
        this.isLoading = false;
      },
      error: (error: Error) => {
        this.isLoading = false;
        this.toastr.error('Failed to load application details');
        this.router.navigate(['/pfe-space/applications']);
      }
    });
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

  goToOffersList(): void {
    this.router.navigate(['/offers']);
  }

  downloadFile(fileType: 'cv' | 'coverLetter'): void {
    if (!this.application?.id) return;

    this.isLoading = true;
    this.applicationService.downloadFile(this.application.id, fileType).subscribe({
      next: (blob: Blob) => {
        // Create a URL for the blob
        const url = window.URL.createObjectURL(blob);
        
        // Create a temporary anchor element
        const a = document.createElement('a');
        a.href = url;
        
        // Set the download filename
        const fileName = `${this.application?.studentName}_${fileType}.pdf`;
        a.download = fileName;
        
        // Trigger the download
        document.body.appendChild(a);
        a.click();
        
        // Clean up
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.isLoading = false;
      },
      error: (error: Error) => {
        this.isLoading = false;
        this.toastr.error('Failed to download file');
        console.error('Error downloading file:', error);
      }
    });
  }

  acceptApplication(): void {
    if (!this.application?.id) return;

    this.applicationService.acceptApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application accepted successfully');
        if (this.application?.id) {
          this.loadApplication(this.application.id);
        }
      },
      error: (error: Error) => {
        this.toastr.error('Failed to accept application');
        console.error('Error accepting application:', error);
      }
    });
  }

  rejectApplication(): void {
    if (!this.application?.id) return;

    this.applicationService.rejectApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application rejected successfully');
        if (this.application?.id) {
          this.loadApplication(this.application.id);
        }
      },
      error: (error: Error) => {
        this.toastr.error('Failed to reject application');
        console.error('Error rejecting application:', error);
      }
    });
  }
} 