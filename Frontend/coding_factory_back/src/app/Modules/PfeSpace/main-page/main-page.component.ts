import { Component , OnInit } from '@angular/core';
import { ContainerComponent } from "../../../Components/container/container.component";
import { SidebarComponent } from "../../../Components/sidebar/sidebar.component";
import { Application} from "src/app/models/application.model"
import { Evaluation } from 'src/app/models/evaluation.model';
import { Project } from 'src/app/models/project.model';
import { HttpClient } from '@angular/common/http';
import { ProjectService } from 'src/app/services/project.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],


 
})
export class MainPageComponent  implements OnInit {
  projectStats: any = {};
  applicationStats: any = {};
  deliverableStats: any = {};
  recentApplications: any[] = [];
  recentEvaluations: any[] = [];
  recentProjects: any[] = [];
  projects: any[] = []; // Pour mapper projectId à project title

  constructor(private projectService: ProjectService , private router: Router) { }
 

  ngOnInit(): void {
    this.loadProjectStats();
    this.loadApplicationStats();
    this.loadDeliverableStats(); 
    this.loadRecentApplications();
    this.loadRecentEvaluations();
    this.loadRecentProjects();
  }
  loadDeliverableStats(): void {
    this.projectService.getDeliverableStats().subscribe(data => {
      this.deliverableStats = data;
    });
  }
  

  loadProjectStats(): void {
    this.projectService.getProjectStats().subscribe(data => {
      this.projectStats = data;
    });
  }

  loadApplicationStats(): void {
    this.projectService.getApplicationStats().subscribe(data => {
      this.applicationStats = data;
    });
  }

  loadRecentApplications(): void {
    this.projectService.getRecentApplications().subscribe(data => {
      this.recentApplications = data;
    });
  }

  loadRecentEvaluations(): void {
    this.projectService.getRecentEvaluations().subscribe(data => {
      this.recentEvaluations = data;
    });
  }

  loadRecentProjects(): void {
    this.projectService.getRecentProjects().subscribe(data => {
      this.recentProjects = data;
      this.projects = data; // Initialiser la liste des projets pour le mapping
    });
  }

  getProjectTitle(projectId: number): string {
    const project = this.projects.find(p => p.id === projectId);
    return project ? project.title : 'Unknown Project';
  }

  getEvaluatorName(deliverableId: number): string {
    // Implémentez cette méthode si vous avez des données sur les évaluateurs
    return 'Evaluator Name';
  }

  getDeliverableTitle(deliverableId: number): string {
    // Implémentez cette méthode si vous avez des données sur les livrables
    return 'Deliverable Title';
  }

  goToProjects() {
    this.router.navigate(['/projects']);
  }

  goToDeliverables() {
    this.router.navigate(['/deliverable-list']);
  }
}
