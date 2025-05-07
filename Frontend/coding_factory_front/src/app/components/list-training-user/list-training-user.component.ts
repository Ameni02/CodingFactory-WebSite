import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Formation, FormationService } from 'src/app/services/formation.service';

@Component({
  selector: 'app-list-training-user',
  templateUrl: './list-training-user.component.html',
  styleUrls: ['./list-training-user.component.css']
})
export class ListTrainingUserComponent implements OnInit {
  formations: Formation[] = [];
  sortOption: string = 'positiveCount'; // Default to sorting by positive comment count

  popup = {
    visible: false,
    type: 'success' as 'success' | 'error' | 'info',
    message: ''
  };

  constructor(
    private formationService: FormationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFormations();
  }

  loadFormations(): void {
    // Always use the positive count sorting for user view
    const observable = this.formationService.getAllNonArchivedFormationsByPositiveCount();

    observable.subscribe({
      next: (data) => {
        this.formations = data;
        console.log('Loaded formations:', data);

        if (data.length === 0) {
          this.showPopup('info', 'No trainings available');
        }
      },
      error: (err) => {
        console.error('Error loading trainings:', err);
        this.showPopup('error', 'Error loading trainings');
        this.formations = []; // Set empty array on error
      }
    });
  }

  downloadPdf(id: number): void {
    this.formationService.getFormationById(id).subscribe({
      next: (formation) => {
        const url = `http://localhost:8057/api/formations/${id}/pdf`;
        const link = document.createElement('a');
        link.href = url;
        link.download = formation.pdfFileName || 'formation.pdf';
        link.target = '_blank';
        link.click();
        this.showPopup('success', 'Download started successfully.');
      },
      error: () => {
        this.showPopup('error', 'Error downloading the file.');
      }
    });
  }

  showPopup(type: 'success' | 'error' | 'info', message: string): void {
    this.popup = { type, message, visible: true };
    setTimeout(() => this.popup.visible = false, 3000);
  }

  viewTrainingDetails(id: number): void {
    console.log('Navigating to training detail with ID:', id);
    this.router.navigate(['/training-detail', id]);
  }
}