import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from 'src/services/project.service';
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
    const isActive = status === 'ACTIVE' || status === 'PENDING';
    
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
    if (status !== 'ACTIVE' && status !== 'PENDING') {
      this.toastr.warning('This project is not currently active');
      return;
    }

    this.router.navigate(['/applications/new'], {
      queryParams: { projectId: this.project.id }
    });
  }

  downloadDescription(): void {
    if (!this.project?.descriptionFilePath) return;

    this.isDownloading = true;
    this.projectService.downloadFile(this.project.id, 'description').subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${this.project.title}_description.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.isDownloading = false;
      },
      error: (error) => {
        this.isDownloading = false;
        this.toastr.error('Failed to download description');
        console.error('Error downloading description:', error);
      }
    });
  }

  goToOffersList(): void {
    this.router.navigate(['/offers']);
  }
}