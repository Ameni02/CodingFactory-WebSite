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
  projects: Project[] = []; // Liste des projets

  constructor(private projectService: ProjectService , private router:Router) {}
 

  goToDetails(projectId: number) {
    this.router.navigate(['/project-details', projectId]);
  }


  ngOnInit(): void {
    this.loadProjects(); // Charge les projets au démarrage du composant
  }

  // Charge la liste des projets depuis l'API
  loadProjects(): void {
    this.projectService.getProjects().subscribe(
      (data) => {
        this.projects = data; // Utilisez les données telles qu'elles sont renvoyées par l'API
        console.log('Projects loaded:', this.projects); // Affichez les données dans la console
      },
      (error) => {
        console.error('Error loading projects:', error); // Affichez les erreurs dans la console
      }
    );
  }

  // Archive un projet
  archiveProject(id: number): void {
    console.log('Archiving project with ID:', id); // Affichez l'ID du projet dans la console
    if (!id) {
      console.error('Project ID is undefined or null'); // Vérifiez que l'ID est valide
      return;
    }
    this.projectService.archiveProject(id).subscribe(
      () => {
        console.log('Project archived successfully'); // Confirmation dans la console
        this.loadProjects(); // Rechargez la liste des projets après archivage
      },
      (error) => {
        console.error('Error archiving project:', error); // Affichez les erreurs dans la console
      }
    );
  }
}