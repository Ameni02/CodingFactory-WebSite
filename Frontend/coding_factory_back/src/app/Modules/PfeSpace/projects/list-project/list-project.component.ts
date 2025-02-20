import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-project',
  templateUrl: './list-project.component.html',
  styleUrls: ['./list-project.component.css']
})
export class ListProjectComponent {

  technologyFilter: string = '';

  // Example data
  projects = [
    {
      id: '1',
      title: 'AI Chatbot',
      description: 'An AI-powered chatbot',
      technologies: 'AI, NLP',
      status: 'In Progress',
    },
    {
      id: '2',
      title: 'E-commerce Platform',
      description: 'Online shopping platform',
      technologies: 'Angular, Node.js',
      status: 'Pending',
    },
  ];

  constructor(private router: Router) {}

  // Filtered projects based on technologyFilter
  get filteredProjects() {
    return this.projects.filter((project) =>
      project.technologies.toLowerCase().includes(this.technologyFilter.toLowerCase())
    );
  }

  // Navigate to project details
  onProjectClick(projectId: string) {
    this.router.navigate(['/project-details', projectId]);
  }
}

