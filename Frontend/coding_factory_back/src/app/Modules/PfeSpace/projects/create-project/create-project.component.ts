import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { Project } from 'src/app/models/project.model';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css'],
})
export class CreateProjectComponent {
  project: Project = {
    id: 0,
    title: '',
    field: '',
    requiredSkills: '',
    descriptionFilePath: '',
    numberOfPositions: 0,
    startDate: new Date(),
    endDate: new Date(),
    companyName: '',
    professionalSupervisor: '',
    companyAddress: '',
    companyEmail: '',
    companyPhone: '',
    archived: false,
  };
  selectedFile: File | null = null;

  constructor(private router: Router, private projectService: ProjectService) {}

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    console.log('File selected:', this.selectedFile); // Debugging log
  }

  onSave(): void {
    if (this.selectedFile) {
      console.log('Selected File:', this.selectedFile); // Log the file

      // Call the addProject method with the project data and file
      this.projectService.addProject(this.project, this.selectedFile).subscribe(
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
      alert('Please select a file before submitting.'); // Handle case where no file is selected
    }
  }
}