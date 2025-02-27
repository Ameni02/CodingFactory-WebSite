import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { Project } from 'src/app/models/project.model';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent implements OnInit {
  project: Project = new Project(); // Initialize with default values
  selectedFile: File | null = null;

  constructor(
    private router: Router,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    // No need to fetch project details in create mode
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSave(): void {
    console.log('Save button clicked'); // Log the button click
    console.log('Project to be saved:', this.project); // Log the project object

    if (this.selectedFile) {
      console.log('File selected:', this.selectedFile.name); // Log the selected file
      this.projectService.uploadFile(this.selectedFile).subscribe(
        (response: { message: string, filePath: string }) => {
          console.log('File uploaded successfully:', response); // Log the file upload response
          this.project.descriptionFilePath = response.filePath; // Update the file path
          this.createProject(); // Call the create method
        },
        (error) => {
          console.error('Error uploading file:', error); // Log the file upload error
          alert('Error uploading file. Please try again.');
        }
      );
    } else {
      console.log('No file selected'); // Log if no file is selected
      this.createProject(); // Call the create method
    }
  }

  private createProject(): void {
    if (this.project) {
      console.log('Project to be saved:', this.project); // Log the project object

      // Create a new project
      this.projectService.addProject(this.project).subscribe(
        (savedProject) => {
          console.log('Project created successfully:', savedProject); // Log the response
          alert('Project created successfully!');
          this.router.navigate(['/projects']); // Navigate to the projects list
        },
        (error) => {
          console.error('Error creating project:', error); // Log the error
          alert('Error creating project. Please try again.');
        }
      );
    } else {
      console.error('Project is undefined. Cannot save.'); // Log the error
      alert('Error: Project data is missing. Cannot save.');
    }
  }
}