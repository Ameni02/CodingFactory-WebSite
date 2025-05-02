import { Component, Input, OnInit } from '@angular/core';
import { Comment } from '../../models/comment.model';
import { CommentService } from '../../services/comment.service';
import { Formation } from '../../models/formation.model';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.scss']
})
export class CommentListComponent implements OnInit {
  @Input() formation!: Formation;
  
  comments: Comment[] = [];
  loading = false;
  error = '';
  
  // Sentiment statistics
  positiveCount = 0;
  neutralCount = 0;
  negativeCount = 0;
  averageRating = 0;

  constructor(private commentService: CommentService) { }

  ngOnInit(): void {
    this.loadComments();
  }

  loadComments(): void {
    if (!this.formation || !this.formation.id) {
      return;
    }
    
    this.loading = true;
    this.commentService.getCommentsByFormationId(this.formation.id).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.loading = false;
        this.calculateStatistics();
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des commentaires: ' + (err.error || err.message);
        this.loading = false;
      }
    });
  }
  
  calculateStatistics(): void {
    if (!this.comments.length) {
      this.positiveCount = 0;
      this.neutralCount = 0;
      this.negativeCount = 0;
      this.averageRating = 0;
      return;
    }
    
    // Count sentiments
    this.positiveCount = this.comments.filter(c => c.sentimentLabel === 'Positive').length;
    this.neutralCount = this.comments.filter(c => c.sentimentLabel === 'Neutral').length;
    this.negativeCount = this.comments.filter(c => c.sentimentLabel === 'Negative').length;
    
    // Calculate average rating
    this.averageRating = this.comments.reduce((sum, comment) => sum + comment.rating, 0) / this.comments.length;
  }
  
  getSentimentClass(sentiment: string | undefined): string {
    if (!sentiment) return '';
    
    switch (sentiment) {
      case 'Positive': return 'positive';
      case 'Neutral': return 'neutral';
      case 'Negative': return 'negative';
      default: return '';
    }
  }
  
  getSentimentIcon(sentiment: string | undefined): string {
    if (!sentiment) return '';
    
    switch (sentiment) {
      case 'Positive': return '😊';
      case 'Neutral': return '😐';
      case 'Negative': return '😞';
      default: return '';
    }
  }
  
  getFormattedDate(date: Date | undefined): string {
    if (!date) return '';
    
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  
  getCategoryLabel(category: string | undefined): string {
    if (!category) return 'Général';
    
    switch (category) {
      case 'Content': return 'Contenu';
      case 'Instructor': return 'Formateur';
      case 'Materials': return 'Matériels';
      case 'General': return 'Général';
      default: return category;
    }
  }
  
  onCommentAdded(comment: Comment): void {
    this.comments.unshift(comment);
    this.calculateStatistics();
  }
  
  refreshComments(): void {
    this.loadComments();
  }
  
  analyzeAllComments(): void {
    if (!this.formation || !this.formation.id) {
      return;
    }
    
    this.loading = true;
    this.commentService.batchAnalyzeComments(this.formation.id).subscribe({
      next: () => {
        this.loadComments();
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'analyse des commentaires: ' + (err.error || err.message);
        this.loading = false;
      }
    });
  }
}
