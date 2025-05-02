import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Comment } from '../../models/comment.model';
import { CommentService } from '../../services/comment.service';
import { Formation } from '../../models/formation.model';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss']
})
export class CommentFormComponent implements OnInit {
  @Input() formation!: Formation;
  @Output() commentAdded = new EventEmitter<Comment>();
  
  commentForm!: FormGroup;
  submitting = false;
  error = '';
  categories = [
    { value: 'Content', label: 'Contenu' },
    { value: 'Instructor', label: 'Formateur' },
    { value: 'Materials', label: 'Matériels' },
    { value: 'General', label: 'Général' }
  ];

  constructor(
    private fb: FormBuilder,
    private commentService: CommentService
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      userName: ['', [Validators.required]],
      category: ['General']
    });
  }

  onSubmit(): void {
    if (this.commentForm.invalid) {
      return;
    }

    this.submitting = true;
    this.error = '';

    const comment: Comment = {
      ...this.commentForm.value,
      formation: {
        id: this.formation.id
      }
    };

    this.commentService.createComment(comment).subscribe({
      next: (newComment) => {
        this.submitting = false;
        this.commentAdded.emit(newComment);
        this.commentForm.reset({
          rating: 5,
          category: 'General'
        });
      },
      error: (err) => {
        this.submitting = false;
        this.error = 'Erreur lors de l\'ajout du commentaire: ' + (err.error || err.message);
      }
    });
  }
}
