import { Component, OnInit } from '@angular/core';
import { Formation, FormationService } from 'src/app/services/formation.service';

@Component({
  selector: 'app-list-training',
  templateUrl: './list-training.component.html',
  styleUrls: ['./list-training.component.css']
})
export class ListTrainingComponent implements OnInit {

  formations: Formation[] = [];

  popup = {
    visible: false,
    message: '',
    type: 'info' as 'success' | 'error' | 'info'
  };

  constructor(private formationService: FormationService) {}

  ngOnInit(): void {
    this.getAllFormations();
  }

  getAllFormations(): void {
    this.formationService.getAllFormations().subscribe({
      next: (data) => this.formations = data,
      error: (err) => {
        console.error('Error loading trainings', err);
        this.showPopup("❌ Failed to load trainings", "error");
      }
    });
  }

  downloadPdf(id: number, fileName: string): void {
    this.formationService.getPdf(id).subscribe({
      next: (data) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName || 'training.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.showPopup("❌ Failed to download PDF", "error");
      }
    });
  }

  archive(id: number): void {
    this.formationService.archiveFormation(id).subscribe({
      next: () => {
        this.showPopup("✅ Training successfully archived", "success");
        this.getAllFormations();
      },
      error: () => {
        this.showPopup("❌ Failed to archive training", "error");
      }
    });
  }

  unarchive(id: number): void {
    this.formationService.unarchiveFormation(id).subscribe({
      next: () => {
        this.showPopup("✅ Training successfully unarchived", "success");
        this.getAllFormations();
      },
      error: () => {
        this.showPopup("❌ Failed to unarchive training", "error");
      }
    });
  }

  showPopup(message: string, type: 'success' | 'error' | 'info'): void {
    this.popup.message = message;
    this.popup.type = type;
    this.popup.visible = true;
    setTimeout(() => this.popup.visible = false, 4000);
  }
}
