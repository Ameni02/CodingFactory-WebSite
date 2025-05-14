import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultationlistComponent } from './consultationlist.component';

describe('ConsultationlistComponent', () => {
  let component: ConsultationlistComponent;
  let fixture: ComponentFixture<ConsultationlistComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConsultationlistComponent]
    });
    fixture = TestBed.createComponent(ConsultationlistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
