import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { Project } from 'src/app/models/project.model';

@Component({
  selector: 'app-modify-project',
  templateUrl: './modify-project.component.html',
  styleUrls: ['./modify-project.component.css']
})
export class ModifyProjectComponent implements OnInit {
  project: Project | undefined;
  selectedFile: File | null = null;

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

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSave(): void {
    if (this.project) {
      if (this.selectedFile) {
        // Upload the file first
        this.projectService.uploadFile(this.selectedFile).subscribe(
          (response: { message: string, filePath: string }) => {
            // Update the project's descriptionFilePath with the returned file path
            this.project!.descriptionFilePath = response.filePath;
            // Now update the project
            this.updateProject();
          },
          (error) => {
            console.error('Error uploading file:', error);
            alert('Error uploading file. Please try again.');
          }
        );
      } else {
        // If no file is selected, just update the project
        this.updateProject();
      }
    }
  }

  private updateProject(): void {
    if (this.project) {
      // Update the project in the backend
      this.projectService.updateProject(this.project.id, this.project).subscribe(
        () => {
          alert('Project updated successfully!');
          // Navigate back to the project details page
          this.router.navigate(['/project-details', this.project?.id]);
        },
        (error) => {
          console.error('Error updating project:', error);
        }
      );
    }
  }
}