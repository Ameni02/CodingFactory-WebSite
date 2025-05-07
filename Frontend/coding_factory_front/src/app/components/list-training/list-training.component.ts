import { Component, OnInit } from '@angular/core';
import { Formation, FormationService } from 'src/app/services/formation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-training',
  templateUrl: './list-training.component.html',
  styleUrls: ['./list-training.component.css']
})
export class ListTrainingComponent implements OnInit {

  formations: Formation[] = [];
  showArchived = false;
  sortOption = 'positiveCount'; // Default to sorting by positive comment count
  loading = false;

  popup = {
    visible: false,
    message: '',
    type: 'info' as 'success' | 'error' | 'info'
  };

  constructor(
    private formationService: FormationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFormations();
  }

  loadFormations(): void {
    this.loading = true;

    // Choose the appropriate service method based on filters
    let serviceMethod;

    if (this.showArchived) {
      // All formations (archived and non-archived)
      switch (this.sortOption) {
        case 'sentiment':
          serviceMethod = this.formationService.getAllFormationsBySentiment();
          break;
        case 'positive':
          serviceMethod = this.formationService.getAllFormationsByPositiveRatio();
          break;
        case 'positiveCount':
          // For all formations, sort by positive comment count
          serviceMethod = this.formationService.getAllFormations();
          serviceMethod.subscribe(formations => {
            this.formations = formations.sort((a, b) => {
              const countA = a.positiveCommentCount || 0;
              const countB = b.positiveCommentCount || 0;
              return countB - countA; // Descending order
            });
            this.loading = false;
          });
          return; // Early return since we're handling the subscription here
        default:
          serviceMethod = this.formationService.getAllFormations();
      }
    } else {
      // Only non-archived formations
      switch (this.sortOption) {
        case 'sentiment':
          serviceMethod = this.formationService.getAllNonArchivedFormationsBySentiment();
          break;
        case 'positive':
          serviceMethod = this.formationService.getAllNonArchivedFormationsByPositiveRatio();
          break;
        case 'positiveCount':
          serviceMethod = this.formationService.getAllNonArchivedFormationsByPositiveCount();
          break;
        default:
          serviceMethod = this.formationService.getAllFormationsNonArchivees();
      }
    }

    // Execute the selected service method
    serviceMethod.subscribe({
      next: (data) => {
        this.formations = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading trainings', err);
        this.showPopup("❌ Failed to load trainings", "error");
        this.loading = false;
      }
    });
  }

  toggleArchived(): void {
    this.showArchived = !this.showArchived;
    this.loadFormations();
  }

  changeSortOption(option: string): void {
    this.sortOption = option;
    this.loadFormations();
  }

  viewFormationDetails(id: number): void {
    console.log('Navigating to training detail with ID:', id);

    // Check if we're in the admin section
    const url = this.router.url;
    if (url.includes('admin')) {
      this.router.navigate(['/admin/training-detail', id]);
    } else {
      this.router.navigate(['/training-detail', id]);
    }
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
        this.loadFormations();
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
        this.loadFormations();
      },
      error: () => {
        this.showPopup("❌ Failed to unarchive training", "error");
      }
    });
  }

  getSentimentIcon(formation: Formation): string {
    if (!formation.averageSentimentScore) return '😐';

    if (formation.averageSentimentScore >= 0.7) {
      return '😊';
    } else if (formation.averageSentimentScore >= 0.4) {
      return '😐';
    } else {
      return '😞';
    }
  }

  getSentimentClass(formation: Formation): string {
    if (!formation.averageSentimentScore) return '';

    if (formation.averageSentimentScore >= 0.7) {
      return 'positive';
    } else if (formation.averageSentimentScore >= 0.4) {
      return 'neutral';
    } else {
      return 'negative';
    }
  }

  showPopup(message: string, type: 'success' | 'error' | 'info'): void {
    this.popup.message = message;
    this.popup.type = type;
    this.popup.visible = true;
    setTimeout(() => this.popup.visible = false, 4000);
  }
}
