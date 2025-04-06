import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlagiarismNotificationComponent } from './plagiarism-notification.component';

describe('PlagiarismNotificationComponent', () => {
  let component: PlagiarismNotificationComponent;
  let fixture: ComponentFixture<PlagiarismNotificationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlagiarismNotificationComponent]
    });
    fixture = TestBed.createComponent(PlagiarismNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
