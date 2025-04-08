import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { ToastrService } from 'ngx-toastr';
import { Application } from 'src/app/models/application.model';

@Component({
  selector: 'app-application-detail',
  templateUrl: './application-detail.component.html',
  styleUrls: ['./application-detail.component.css']
})
export class ApplicationDetailComponent implements OnInit {
  application: any;
  isDownloading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (!id) {
      this.toastr.error('No application ID provided');
      this.router.navigate(['/pfe-space/offers']);
      return;
    }
    this.loadApplication(id);
  }

  loadApplication(id: number): void {
    this.applicationService.getApplication(id).subscribe({
      next: (data: Application) => {
        if (!data) {
          this.toastr.error('Application not found');
          this.router.navigate(['/offers']);
          return;
        }
        this.application = data;
        console.log('Application loaded:', this.application); // For debugging
      },
      error: (error: Error) => {
        console.error('Error loading application:', error);
        this.toastr.error('Failed to load application details');
        this.router.navigate(['/offers']);
      }
    });
  }

  goToOffersList(): void {
    this.router.navigate(['/offers']);
  }

  downloadFile(fileType: 'cv' | 'coverLetter'): void {
    if (!this.application) return;

    this.isDownloading = true;
    this.applicationService.downloadFile(this.application.id, fileType).subscribe({
        next: (blob: Blob) => {
            // Create a URL for the blob
            const url = window.URL.createObjectURL(blob);
            
            // Create a temporary anchor element
            const a = document.createElement('a');
            a.href = url;
            
            // Set the download filename
            const fileName = `${this.application.studentName}_${fileType}.pdf`;
            a.download = fileName;
            
            // Trigger the download
            document.body.appendChild(a);
            a.click();
            
            // Clean up
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            this.isDownloading = false;
        },
        error: (error: Error) => {
            this.isDownloading = false;
            this.toastr.error('Failed to download file');
            console.error('Error downloading file:', error);
        }
    });
}

  acceptApplication(): void {
    if (!this.application) return;

    this.applicationService.acceptApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application accepted successfully');
        this.loadApplication(this.application.id);
      },
      error: (error: Error) => {
        this.toastr.error('Failed to accept application');
        console.error('Error accepting application:', error);
      }
    });
  }

  rejectApplication(): void {
    if (!this.application) return;

    this.applicationService.rejectApplication(this.application.id).subscribe({
      next: () => {
        this.toastr.success('Application rejected successfully');
        this.loadApplication(this.application.id);
      },
      error: (error: Error) => {
        this.toastr.error('Failed to reject application');
        console.error('Error rejecting application:', error);
      }
    });
  }
} 