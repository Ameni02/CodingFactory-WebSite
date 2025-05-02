import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormationService } from '../../services/formation.service';
import { Formation } from '../../models/formation.model';
import { Comment } from '../../models/comment.model';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-formation-detail',
  templateUrl: './formation-detail.component.html',
  styleUrls: ['./formation-detail.component.scss']
})
export class FormationDetailComponent implements OnInit {
  formation: Formation | null = null;
  loading = false;
  error = '';
  pdfUrl: SafeResourceUrl | null = null;
  activeTab = 'details';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formationService: FormationService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.loadFormation();
  }

  loadFormation(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'ID de formation non valide';
      return;
    }
    
    this.loading = true;
    this.formationService.getFormationById(Number(id)).subscribe({
      next: (formation) => {
        this.formation = formation;
        this.loading = false;
        this.loadPdf();
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la formation: ' + (err.error || err.message);
        this.loading = false;
      }
    });
  }

  loadPdf(): void {
    if (!this.formation) return;
    
    this.formationService.getPdf(this.formation.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement du PDF: ' + (err.error || err.message);
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  onCommentAdded(comment: Comment): void {
    if (!this.formation) return;
    
    // Refresh formation to get updated sentiment metrics
    this.formationService.getFormationById(this.formation.id).subscribe({
      next: (formation) => {
        this.formation = formation;
      },
      error: (err) => {
        console.error('Error refreshing formation:', err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/formations']);
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
