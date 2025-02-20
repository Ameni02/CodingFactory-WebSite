import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent {
    // Define the project object
    project = {
      title: '',
      description: '',
      technologies: '',
      teamMembers: '',
      attachments: [] as File[], // Array to store uploaded files
    };
  
    constructor(private router: Router) {}
  
    // Handle file input change
    onFileChange(event: any) {
      const files = event.target.files;
      if (files && files.length > 0) {
        this.project.attachments = Array.from(files); // Convert FileList to Array
      }
    }
  
    // Handle form submission
    onSubmit() {
      // Validate required fields
      if (!this.project.title || !this.project.description || !this.project.technologies || !this.project.teamMembers) {
        alert('Please fill out all required fields.');
        return;
      }
  
      // Log the project data (for demonstration purposes)
      console.log('Project Created:', this.project);
  
      // Reset the form
      this.project = {
        title: '',
        description: '',
        technologies: '',
        teamMembers: '',
        attachments: [],
      };
  
      // Navigate to the project list page
      this.router.navigate(['/projects']);
    }

}
