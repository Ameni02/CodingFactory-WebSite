import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from 'src/services/project.service';
import { Project } from 'src/app/models/project.model';

@Component({
  selector: 'app-offer-detail',
  templateUrl: './offer-detail.component.html',
  styleUrls: ['./offer-detail.component.css'],
})
export class OfferDetailComponent implements OnInit {
  project: Project | null = null; // Store the fetched project

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    const offerId = this.route.snapshot.paramMap.get('id'); // Get the ID from URL

    if (offerId) {
      this.projectService.getProjectById(+offerId).subscribe({
        next: (data) => {
          this.project = data;
          console.log('Project details:', this.project);
        },
        error: (error) => {
          console.error('Error fetching project details:', error);
          alert('Failed to load project details.');
        },
      });
    }
  }

  onDownload(filePath: string): void {
    this.projectService.downloadFile(filePath).subscribe(
      (data: Blob) => {
        // Create a blob URL for the file
        const blobUrl = URL.createObjectURL(data);
        // Create a link element and trigger the download
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = filePath.split('/').pop() || 'file'; // Use the file name from the path
        link.click();
        // Clean up the blob URL
        URL.revokeObjectURL(blobUrl);
      },
      (error) => {
        console.error('Error downloading file:', error);
        alert('Error downloading file. Please try again.');
      }
    );
  } 
}
