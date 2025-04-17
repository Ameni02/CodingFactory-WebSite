import { Component } from '@angular/core';
import { Formation, FormationService } from 'src/app/services/formation.service';

@Component({
  selector: 'app-list-training-user',
  templateUrl: './list-training-user.component.html',
  styleUrls: ['./list-training-user.component.css']
})
export class ListTrainingUserComponent {
  formations: Formation[] = [];

  popup = {
    visible: false,
    type: 'success',
    message: ''
  };

  constructor(private formationService: FormationService) {}

  ngOnInit(): void {
    this.loadFormations();
  }

  loadFormations(): void {
    this.formationService.getAllFormationsNonArchivees().subscribe({
      next: (data) => this.formations = data,
      error: (err) => {
        this.showPopup('error', 'Erreur lors du chargement des formations.');
        console.error(err);
      }
    });
  }

  downloadPdf(id: number): void {
    this.formationService.getFormationById(id).subscribe({
      next: (formation) => {
        const url = `http://localhost:8080/api/formations/${id}/pdf`; // même endpoint que l'admin
        const link = document.createElement('a');
        link.href = url;
        link.download = formation.pdfFileName || 'formation.pdf';
        link.target = '_blank';
        link.click();
        this.showPopup('success', 'Téléchargement lancé avec succès.');
      },
      error: () => {
        this.showPopup('error', 'Erreur lors du téléchargement.');
      }
    });
  }

  showPopup(type: 'success' | 'error', message: string): void {
    this.popup = { type, message, visible: true };
    setTimeout(() => this.popup.visible = false, 3000);
  }
}