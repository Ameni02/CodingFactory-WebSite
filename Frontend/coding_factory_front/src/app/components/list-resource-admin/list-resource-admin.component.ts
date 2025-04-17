import { Component, OnInit } from '@angular/core';
import { EducationalResourceService, RessourcePedagogique } from 'src/app/services/educational-resource.service';

@Component({
  selector: 'app-list-resource-admin',
  templateUrl: './list-resource-admin.component.html',
  styleUrls: ['./list-resource-admin.component.css']
})
export class ListResourceAdminComponent implements OnInit {
  ressources: RessourcePedagogique[] = [];

  constructor(private resourceService: EducationalResourceService) {}

  ngOnInit(): void {
    this.loadAllResources();
  }

  loadAllResources(): void {
    this.resourceService.getAllRessources().subscribe({
      next: (data: RessourcePedagogique[]) => {
        this.ressources = data;
      },
      error: (err: any) => {
        console.error('Erreur lors du chargement des ressources', err);
      }
    });
  }

  downloadResource(id: number): void {
    this.resourceService.getRessourceById(id).subscribe({
      next: (ressource: RessourcePedagogique) => {
        const fileUrl = ressource.cheminFichier;
        if (!fileUrl) {
          console.error('Chemin du fichier introuvable');
          return;
        }

        const fileName = this.getFileName(fileUrl);
        this.triggerDownload(fileUrl, fileName);
      },
      error: (err: any) => {
        console.error('Erreur lors de la récupération de la ressource', err);
      }
    });
  }

  triggerDownload(url: string, fileName: string): void {
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.target = '_blank';
    link.click();
  }

  getFileName(path: string): string {
    return path.split('/').pop() || 'resource.pdf';
  }
}
