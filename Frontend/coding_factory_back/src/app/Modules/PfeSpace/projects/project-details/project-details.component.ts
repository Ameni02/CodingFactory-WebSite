import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

interface Project {
  id: string;
  title: string;
  description: string;
  technologies: string;
  teamMembers: string;
  status: string;
}

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.css']
})
export class ProjectDetailsComponent  {
  project!: Project;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('id');
    this.project = this.getProjectById(projectId);
  }

  // Mock function to get project by ID
  getProjectById(id: string | null): Project {
    const projects: Project[] = [
      {
        id: '1',
        title: 'AI Chatbot',
        description: 'An AI-powered chatbot',
        technologies: 'AI, NLP',
        teamMembers: 'John Doe, Jane Smith',
        status: 'In Progress',
      },
      {
        id: '2',
        title: 'E-commerce Platform',
        description: 'Online shopping platform',
        technologies: 'Angular, Node.js',
        teamMembers: 'Alice Johnson, Bob Brown',
        status: 'Pending',
      },
    ];

    return projects.find((project) => project.id === id) || projects[0];
  }

  // Update Project
  onUpdate() {
    this.router.navigate(['/update-project', this.project.id]);
  }

  // Archive Project
  onArchive() {
    alert(`Project "${this.project.title}" has been archived.`);
    this.router.navigate(['/projects']);
  }

  // Delete Project
  onDelete() {
    if (confirm(`Are you sure you want to delete "${this.project.title}"?`)) {
      alert(`Project "${this.project.title}" has been deleted.`);
      this.router.navigate(['/projects']);
    }
  }
}


