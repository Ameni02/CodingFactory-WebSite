import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-offer-detail',
  templateUrl: './offer-detail.component.html',
  styleUrls: ['./offer-detail.component.css']
})
export class OfferDetailComponent implements OnInit {
  project: any;
  isDownloading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.loadProject(id);
  }

  loadProject(id: number): void {
    this.projectService.getProjectById(id).subscribe({
      next: (data) => {
        this.project = data;
        console.log('Project status:', this.project.status);
      },
      error: (error) => {
        this.toastr.error('Failed to load project details');
        console.error('Error loading project:', error);
      }
    });
  }

  canApply(): boolean {
    if (!this.project) return false;

    const status = this.project.status?.toUpperCase();
    const isActive = status === 'ACTIVE' || status === 'PENDING' || status === 'IN_PROGRESS';

    return (
      !this.project.archived &&
      this.project.numberOfPositions > 0 &&
      isActive
    );
  }

  onApply(): void {
    if (!this.project) return;

    if (this.project.archived) {
      this.toastr.warning('This project is no longer accepting applications');
      return;
    }

    if (this.project.numberOfPositions <= 0) {
      this.toastr.warning('No positions available for this project');
      return;
    }

    const status = this.project.status?.toUpperCase();
    if (status !== 'ACTIVE' && status !== 'PENDING' && status !== 'IN_PROGRESS') {
      this.toastr.warning('This project is not currently active');
      return;
    }

    this.router.navigate(['/pfe-space/applications/new'], {
      queryParams: { projectId: this.project.id }
    });
  }

  downloadDescription(): void {
    if (!this.project?.id) {
      this.toastr.warning('Project details not available');
      return;
    }

    console.log('Downloading description for project ID:', this.project.id);
    if (this.project.descriptionFilePath) {
      console.log('Description file path:', this.project.descriptionFilePath);
    } else {
      console.log('No description file path available, will try to download anyway');
    }

    this.isDownloading = true;
    this.projectService.downloadFile(this.project.id, 'description').subscribe({
      next: (blob: Blob) => {
        console.log('Download successful, blob size:', blob.size);
        console.log('Content type:', blob.type);

        if (blob.size === 0) {
          this.isDownloading = false;
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

        a.download = `${this.project.title}_description${fileExtension}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.isDownloading = false;
        this.toastr.success('Description downloaded successfully');
      },
      error: (error: any) => {
        this.isDownloading = false;
        this.toastr.error('Failed to download description: ' + (error.message || 'Unknown error'));
        console.error('Error downloading description:', error);
      }
    });
  }

  goToOffersList(): void {
    this.router.navigate(['/pfe-space/offers']);
  }
}
