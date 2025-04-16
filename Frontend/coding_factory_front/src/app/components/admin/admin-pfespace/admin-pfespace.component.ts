import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

// Import models
import { Project } from '../../../Modules/PfeSpace/models/project.model';
import { Application } from '../../../Modules/PfeSpace/models/application.model';
import { Deliverable } from '../../../Modules/PfeSpace/models/deliverable.model';
import { Evaluation } from '../../../Modules/PfeSpace/models/evaluation.model';

@Component({
  selector: 'app-admin-pfespace',
  templateUrl: './admin-pfespace.component.html',
  styleUrls: ['./admin-pfespace.component.css']
})
export class AdminPfeSpaceComponent implements OnInit {
  activeTab: string = 'dashboard';

  // API URL
  private apiUrl = 'http://localhost:8080/pfespace/api/pfe';

  // Statistics
  projectStats: any = {};
  applicationStats: any = {};
  deliverableStats: any = {};

  // Data lists
  recentProjects: Project[] = [];
  recentApplications: Application[] = [];
  recentDeliverables: Deliverable[] = [];
  recentEvaluations: Evaluation[] = [];

  // Full data lists for tables
  allProjects: Project[] = [];
  allApplications: Application[] = [];
  allDeliverables: Deliverable[] = [];
  allEvaluations: Evaluation[] = [];

  // Selected items for details
  selectedProject: Project | null = null;
  selectedApplication: Application | null = null;
  selectedDeliverable: Deliverable | null = null;
  selectedEvaluation: Evaluation | null = null;

  // Show details flags
  showProjectDetails = false;
  showApplicationDetails = false;
  showDeliverableDetails = false;
  showEvaluationDetails = false;

  // Loading and error states
  loading = {
    stats: false,
    projects: false,
    applications: false,
    deliverables: false,
    evaluations: false
  };

  error = {
    stats: null as string | null,
    projects: null as string | null,
    applications: null as string | null,
    deliverables: null as string | null,
    evaluations: null as string | null
  };

  constructor(
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.loadAllData();
  }

  loadDashboardData(): void {
    this.loadProjectStats();
    this.loadApplicationStats();
    this.loadDeliverableStats();
    this.loadRecentProjects();
    this.loadRecentApplications();
    this.loadRecentEvaluations();
  }

  loadAllData(): void {
    this.loadAllProjects();
    this.loadAllApplications();
    this.loadAllDeliverables();
    this.loadAllEvaluations();
  }

  // Stats loading methods
  loadProjectStats(): void {
    this.loading.stats = true;
    // Using the correct endpoint based on the backend implementation
    this.http.get<any>(`${this.apiUrl}/projects/project-stats`).subscribe({
      next: (data: any) => {
        this.projectStats = data;
        this.loading.stats = false;
      },
      error: (err: any) => {
        console.error('Error loading project stats:', err);
        this.error.stats = 'Failed to load project statistics';
        this.loading.stats = false;
      }
    });
  }

  loadApplicationStats(): void {
    // Using the correct endpoint based on the backend implementation
    this.http.get<any>(`${this.apiUrl}/projects/application-stats`).subscribe({
      next: (data: any) => {
        this.applicationStats = data;
      },
      error: (err: any) => {
        console.error('Error loading application stats:', err);
      }
    });
  }

  loadDeliverableStats(): void {
    // Using the correct endpoint based on the backend implementation
    this.http.get<any>(`${this.apiUrl}/projects/deliverable-stats`).subscribe({
      next: (data: any) => {
        this.deliverableStats = data;
      },
      error: (err: any) => {
        console.error('Error loading deliverable stats:', err);
      }
    });
  }

  // Recent data loading methods
  loadRecentProjects(): void {
    this.loading.projects = true;
    // Using the correct endpoint based on the backend implementation
    this.http.get<any[]>(`${this.apiUrl}/projects/recent-projects`).subscribe({
      next: (data: any[]) => {
        this.recentProjects = data;
        this.loading.projects = false;
      },
      error: (err: any) => {
        console.error('Error loading recent projects:', err);
        this.error.projects = 'Failed to load recent projects';
        this.loading.projects = false;
      }
    });
  }

  loadRecentApplications(): void {
    this.loading.applications = true;
    // Using the correct endpoint based on the backend implementation
    this.http.get<any[]>(`${this.apiUrl}/projects/recent-applications`).subscribe({
      next: (data: any[]) => {
        this.recentApplications = data;
        this.loading.applications = false;
      },
      error: (err: any) => {
        console.error('Error loading recent applications:', err);
        this.error.applications = 'Failed to load recent applications';
        this.loading.applications = false;
      }
    });
  }

  loadRecentEvaluations(): void {
    this.loading.evaluations = true;
    // Using the correct endpoint based on the backend implementation
    this.http.get<any[]>(`${this.apiUrl}/projects/recent-evaluations`).subscribe({
      next: (data: any[]) => {
        this.recentEvaluations = data;
        this.loading.evaluations = false;
      },
      error: (err: any) => {
        console.error('Error loading recent evaluations:', err);
        this.error.evaluations = 'Failed to load recent evaluations';
        this.loading.evaluations = false;
      }
    });
  }

  // Load all data methods
  loadAllProjects(): void {
    this.http.get<Project[]>(`${this.apiUrl}/projects`).subscribe({
      next: (data: Project[]) => {
        this.allProjects = data;
      },
      error: (err: any) => {
        console.error('Error loading all projects:', err);
      }
    });
  }

  loadAllApplications(): void {
    this.http.get<Application[]>(`${this.apiUrl}/applications`).subscribe({
      next: (data: Application[]) => {
        this.allApplications = data;
      },
      error: (err: any) => {
        console.error('Error loading all applications:', err);
      }
    });
  }

  loadAllDeliverables(): void {
    this.http.get<Deliverable[]>(`${this.apiUrl}/deliverables`).subscribe({
      next: (data: Deliverable[]) => {
        this.allDeliverables = data;
      },
      error: (err: any) => {
        console.error('Error loading all deliverables:', err);
      }
    });
  }

  loadAllEvaluations(): void {
    this.http.get<Evaluation[]>(`${this.apiUrl}/evaluations`).subscribe({
      next: (data: Evaluation[]) => {
        this.allEvaluations = data;
      },
      error: (err: any) => {
        console.error('Error loading all evaluations:', err);
      }
    });
  }

  // Archive/Unarchive methods
  archiveProject(id: number): void {
    this.http.put<void>(`${this.apiUrl}/projects/${id}/archive`, {}).subscribe({
      next: () => {
        // Update the project in the list
        const project = this.allProjects.find(p => p.id === id);
        if (project) {
          project.archived = true;
        }
      },
      error: (err: any) => {
        console.error('Error archiving project:', err);
      }
    });
  }

  unarchiveProject(id: number): void {
    this.http.put<void>(`${this.apiUrl}/projects/${id}/unarchive`, {}).subscribe({
      next: () => {
        // Update the project in the list
        const project = this.allProjects.find(p => p.id === id);
        if (project) {
          project.archived = false;
        }
      },
      error: (err: any) => {
        console.error('Error unarchiving project:', err);
      }
    });
  }

  archiveApplication(id: number): void {
    this.http.put<void>(`${this.apiUrl}/applications/${id}/archive`, {}).subscribe({
      next: () => {
        // Update the application in the list
        const application = this.allApplications.find(a => a.id === id);
        if (application) {
          application.archived = true;
        }
      },
      error: (err: any) => {
        console.error('Error archiving application:', err);
      }
    });
  }

  archiveDeliverable(id: number): void {
    this.http.put<void>(`${this.apiUrl}/deliverables/${id}/archive`, {}).subscribe({
      next: () => {
        // Update the deliverable in the list
        const deliverable = this.allDeliverables.find(d => d.id === id);
        if (deliverable) {
          deliverable.archived = true;
        }
      },
      error: (err: any) => {
        console.error('Error archiving deliverable:', err);
      }
    });
  }

  archiveEvaluation(id: number): void {
    this.http.put<void>(`${this.apiUrl}/evaluations/${id}/archive`, {}).subscribe({
      next: () => {
        // Update the evaluation in the list
        const evaluation = this.allEvaluations.find(e => e.id === id);
        if (evaluation) {
          evaluation.archived = true;
        }
      },
      error: (err: any) => {
        console.error('Error archiving evaluation:', err);
      }
    });
  }

  // Detail view methods
  viewProjectDetails(project: Project): void {
    this.selectedProject = project;
    this.showProjectDetails = true;
  }

  viewApplicationDetails(application: Application): void {
    this.selectedApplication = application;
    this.showApplicationDetails = true;
  }

  viewDeliverableDetails(deliverable: Deliverable): void {
    this.selectedDeliverable = deliverable;
    this.showDeliverableDetails = true;
  }

  viewEvaluationDetails(evaluation: Evaluation): void {
    this.selectedEvaluation = evaluation;
    this.showEvaluationDetails = true;
  }

  closeDetails(): void {
    this.showProjectDetails = false;
    this.showApplicationDetails = false;
    this.showDeliverableDetails = false;
    this.showEvaluationDetails = false;
  }

  // Navigation methods
  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  // Action methods
  createNewProject(): void {
    this.router.navigate(['/pfe-space/offers/add']);
  }

  viewAllProjects(): void {
    this.setActiveTab('projects');
  }

  viewAllApplications(): void {
    this.setActiveTab('applications');
  }

  viewAllDeliverables(): void {
    this.setActiveTab('deliverables');
  }

  getStatusClass(status: string): string {
    switch(status?.toUpperCase()) {
      case 'PENDING':
        return 'status-warning';
      case 'IN_PROGRESS':
      case 'ACTIVE':
        return 'status-info';
      case 'COMPLETED':
      case 'ACCEPTED':
      case 'APPROVED':
        return 'status-success';
      case 'REJECTED':
      case 'FAILED':
        return 'status-danger';
      default:
        return 'status-pending';
    }
  }

  viewAllEvaluations(): void {
    this.setActiveTab('evaluations');
  }
}
