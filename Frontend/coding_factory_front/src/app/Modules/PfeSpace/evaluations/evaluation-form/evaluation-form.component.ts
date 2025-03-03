import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EvaluationService } from 'src/services/evaluation.service'; 
import { Evaluation } from 'src/app/models/evaluation.model'; 
import { Router } from '@angular/router';

@Component({
  selector: 'app-evaluation-form',
  templateUrl: './evaluation-form.component.html',
  styleUrls: ['./evaluation-form.component.css'],
})
export class EvaluationFormComponent implements OnInit {
  evaluationForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private evaluationService: EvaluationService,
    private router: Router
  ) {
    this.evaluationForm = this.fb.group({
      deliverableId: ['', Validators.required],
      grade: ['', [Validators.required, Validators.min(0), Validators.max(100)]],
      comment: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.evaluationForm.valid) {
      const newEvaluation: Evaluation = this.evaluationForm.value;
      this.evaluationService.addEvaluation(newEvaluation).subscribe({
        next: (response) => {
          console.log('Evaluation submitted successfully!', response);
          this.router.navigate(['/evaluations']);
        },
        error: (error) => {
          console.error('Error submitting evaluation:', error);
        },
      });
    }
  }
}