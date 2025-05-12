import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizListWitoutQuestionsComponent } from './quiz-list-without-questions.component';

describe('QuizListComponent', () => {
  let component: QuizListWitoutQuestionsComponent;
  let fixture: ComponentFixture<QuizListWitoutQuestionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [QuizListWitoutQuestionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuizListWitoutQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
