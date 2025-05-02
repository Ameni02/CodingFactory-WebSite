import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Comment, Formation, FormationService } from 'src/app/services/formation.service';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.css']
})
export class CommentFormComponent implements OnInit {
  @Input() formationId!: number;
  @Output() commentAdded = new EventEmitter<Comment>();

  commentForm!: FormGroup;
  submitting = false;
  error = '';
  categories = [
    { value: 'Content', label: 'Content' },
    { value: 'Instructor', label: 'Instructor' },
    { value: 'Materials', label: 'Materials' },
    { value: 'General', label: 'General' }
  ];

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService
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
        id: this.formationId
      }
    };

    console.log('Submitting comment:', comment);

    // Create a copy of the comment for local use
    const localComment: Comment = {
      ...comment,
      id: new Date().getTime(), // Use timestamp as temporary ID
      createdAt: new Date(),
      sentimentLabel: 'Positive', // Default sentiment
      sentimentScore: 0.8 // Default score
    };

    this.formationService.addComment(comment).subscribe({
      next: (newComment) => {
        console.log('Comment added successfully:', newComment);
        this.submitting = false;

        // Emit the comment to parent component
        this.commentAdded.emit(newComment);

        // Reset form with default values
        this.commentForm.reset();

        // Set default values after reset
        setTimeout(() => {
          this.commentForm.patchValue({
            rating: 5,
            category: 'General',
            userName: '',
            content: ''
          });
        }, 0);

        // Show success message
        alert('Comment added successfully!');
      },
      error: (err) => {
        console.error('Error from addComment subscription:', err);

        // Check if it's a 201 Created status (which is success but Angular treats as error)
        if (err.status === 201) {
          console.log('Comment created with 201 status, treating as success');

          // Emit the local comment to parent component
          this.commentAdded.emit(localComment);

          // Reset form with default values
          this.commentForm.reset();

          // Set default values after reset
          setTimeout(() => {
            this.commentForm.patchValue({
              rating: 5,
              category: 'General',
              userName: '',
              content: ''
            });
          }, 0);

          // Show success message
          alert('Comment added successfully!');

          // Reset submitting state
          this.submitting = false;
        } else {
          // For any other error, still emit the local comment
          // This ensures the UI shows something even if the API call fails
          console.log('Emitting local comment despite error');
          this.commentAdded.emit(localComment);

          // Reset form with default values
          this.commentForm.reset();

          // Set default values after reset
          setTimeout(() => {
            this.commentForm.patchValue({
              rating: 5,
              category: 'General',
              userName: '',
              content: ''
            });
          }, 0);

          // Show error message
          this.error = 'Error adding comment: ' + (err.error || err.message);
          this.submitting = false;

          // Show error alert but mention the comment will still appear locally
          alert('There was an error saving the comment to the database, but it will appear in the list temporarily.');
        }
      }
    });
  }
}
