import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/app/services/project.service';
import { Project } from 'src/app/models/project.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-project',
  templateUrl: './list-project.component.html',
  styleUrls: ['./list-project.component.css'],
})
export class ListProjectComponent implements OnInit {
  projects: Project[] = []; // List of projects

  constructor(private projectService: ProjectService, private router: Router) {}

  ngOnInit(): void {
    this.loadProjects(); // Load the list of projects when the component is initialized
  }

  // Load projects from the API
  loadProjects(): void {
    this.projectService.getProjects().subscribe(
      (data) => {
        this.projects = data;
        console.log('Projects loaded:', this.projects); // Log data for debugging
      },
      (error) => {
        console.error('Error loading projects:', error); // Log errors
      }
    );
  }
// Archive a project
archiveProject(id: number): void {
  console.log('Archiving project with ID:', id);
  if (!id) {
    console.error('Project ID is undefined or null');
    return;
  }
  this.projectService.archiveProject(id).subscribe(
    () => {
      console.log('Project archived successfully');
      
      // Update the project's archived status locally
      const project = this.projects.find(p => p.id === id);
      if (project) {
        project.archived = true;
      }
    },
    (error) => {
      console.error('Error archiving project:', error);
    }
  );
}

// Unarchive a project
unarchiveProject(id: number): void {
  console.log('Unarchiving project with ID:', id);
  if (!id) {
    console.error('Project ID is undefined or null');
    return;
  }
  this.projectService.unarchiveProject(id).subscribe(
    () => {
      console.log('Project unarchived successfully');
     
      
      // Update the project's archived status locally
      const project = this.projects.find(p => p.id === id);
      if (project) {
        project.archived = false;
      }
    },
    (error) => {
      console.error('Error unarchiving project:', error);
    }
  );
}

  // Navigate to project details page
  goToDetails(projectId: number) {
    this.router.navigate(['/project-details', projectId]);
  }

  // Navigate to the create project page
  navigateToCreateProject() {
    this.router.navigate(['/create-project']);
  }
}
