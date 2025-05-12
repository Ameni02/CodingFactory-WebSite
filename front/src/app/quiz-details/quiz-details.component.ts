import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../quiz/quiz.service';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';

interface Option {
  text: string;
  isCorrect: boolean;
}

interface Question {
  text: string;
  type: string;
  options: Option[];
}

@Component({
  selector: 'app-quiz-details',
  standalone: false,
  templateUrl: './quiz-details.component.html',
  styleUrls: ['./quiz-details.component.css'],
})
export class QuizDetailsComponent implements OnInit {
  quiz: any;
  quizForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService,
    private fb: FormBuilder,
    private router: Router
  ) {
    // Initialize the quiz form here (constructor)
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      questions: this.fb.array([]), // Initialize empty array for questions
    });
  }

  ngOnInit(): void {
    const quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadQuizDetails(quizId); // Load quiz details on initialization
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  // Add an option to a specific question
  addOption(questionIndex: number): void {
    const optionGroup = this.fb.group({
      text: ['', Validators.required],
      isCorrect: [false],
    });
    const options = this.questions.at(questionIndex).get('options') as FormArray;
    options.push(optionGroup);
  }

  // Remove a question
  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  // Add a new question to the form
  addQuestion(): void {
    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      type: ['', Validators.required],
      options: this.fb.array([]), // Initialize options as an empty FormArray
    });
    this.questions.push(questionGroup);
  }

  // Load quiz details from the server
  loadQuizDetails(id: number): void {
    this.quizService.getQuizWithDetails(id).subscribe(
      (data) => {
        this.quiz = data;
        this.quizForm.patchValue({
          title: data.title,
          description: data.description,
        });

        // Populate the questions FormArray with the existing quiz questions
        data.questions.forEach((question: Question) => {
          const questionGroup = this.fb.group({
            text: [question.text, Validators.required],
            type: [question.type, Validators.required],
            options: this.fb.array(
              question.options.map((option: Option) => {
                return this.fb.group({
                  text: [option.text, Validators.required],
                  isCorrect: [option.isCorrect],
                });
              })
            ),
          });
          this.questions.push(questionGroup);
        });
      },
      (error) => {
        console.error('Error loading quiz details:', error);
      }
    );
  }

  // Handle form submission
  onSubmit(): void {
    if (this.quizForm.valid) {
      const quizDto = {
        title: this.quizForm.value.title,
        description: this.quizForm.value.description,
        questions: this.quizForm.value.questions.map((q: Question) => ({
          text: q.text,
          type: q.type,
          options: q.options.map((o: Option) => ({
            text: o.text,
            isCorrect: o.isCorrect,
          })),
        })),
      };

      this.quizService.updateQuiz(this.quiz.id, quizDto).subscribe(
        (response) => {
          console.log('Quiz updated successfully:', response);
          this.router.navigate(['/quizzes']); // Navigate after successful submission
        },
        (error) => {
          console.error('Error updating quiz:', error);
        }
      );
    }
  }
}
