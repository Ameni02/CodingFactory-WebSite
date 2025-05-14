import { Component, OnInit } from '@angular/core';
import { ActivatedRoute , Router } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service'; 
import { Project } from 'src/app/models/project.model'; 

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.css']
})
export class ProjectDetailsComponent implements OnInit {
  project: Project | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    // Get the project ID from the route parameters
    const projectId = this.route.snapshot.paramMap.get('id');

    if (projectId) {
      // Fetch the project details from the backend
      this.projectService.getProjectById(+projectId).subscribe(
        (data: Project) => {
          this.project = data;
        },
        (error) => {
          console.error('Error fetching project details:', error);
        }
      );
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
  onUpdate(): void {
    // Navigate to the modify-project route
    if (this.project) {
      this.router.navigate(['/modify-project', this.project.id]);
    }
  }
  onArchive(): void {
    // Implement archive logic
    console.log('Archive button clicked');
  }

  onDelete(): void {
    // Implement delete logic
    console.log('Delete button clicked');
  }
}