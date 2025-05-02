import { Component, OnInit } from '@angular/core';
import { FormationService } from '../../services/formation.service';
import { Formation } from '../../models/formation.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-formation-list',
  templateUrl: './formation-list.component.html',
  styleUrls: ['./formation-list.component.scss']
})
export class FormationListComponent implements OnInit {
  formations: Formation[] = [];
  loading = false;
  error = '';
  showArchived = false;
  sortOption = 'default';

  constructor(
    private formationService: FormationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadFormations();
  }

  loadFormations(): void {
    this.loading = true;
    this.error = '';
    
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
        default:
          serviceMethod = this.formationService.getAllFormationsNonArchivees();
      }
    }
    
    // Execute the selected service method
    serviceMethod.subscribe({
      next: (formations) => {
        this.formations = formations;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des formations: ' + (err.error || err.message);
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

  viewFormation(formation: Formation): void {
    this.router.navigate(['/formations', formation.id]);
  }

  archiveFormation(event: Event, formation: Formation): void {
    event.stopPropagation();
    
    this.formationService.archiveFormation(formation.id).subscribe({
      next: () => {
        formation.archived = true;
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'archivage de la formation: ' + (err.error || err.message);
      }
    });
  }

  unarchiveFormation(event: Event, formation: Formation): void {
    event.stopPropagation();
    
    this.formationService.unarchiveFormation(formation.id).subscribe({
      next: () => {
        formation.archived = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du désarchivage de la formation: ' + (err.error || err.message);
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
}
