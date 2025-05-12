import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiQuizSuggestionsComponent } from './ai-quiz-suggestions.component';

describe('AiQuizSuggestionsComponent', () => {
  let component: AiQuizSuggestionsComponent;
  let fixture: ComponentFixture<AiQuizSuggestionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AiQuizSuggestionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AiQuizSuggestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
