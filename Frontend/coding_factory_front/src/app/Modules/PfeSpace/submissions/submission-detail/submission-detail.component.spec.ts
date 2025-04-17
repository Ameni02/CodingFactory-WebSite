import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmissionDetailComponent } from './submission-detail.component';

describe('SubmissionDetailComponent', () => {
  let component: SubmissionDetailComponent;
  let fixture: ComponentFixture<SubmissionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubmissionDetailComponent]
    });
    fixture = TestBed.createComponent(SubmissionDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
