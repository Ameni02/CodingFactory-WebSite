import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateConsultantComponent } from './create-consultant.component';

describe('CreateConsultantComponent', () => {
  let component: CreateConsultantComponent;
  let fixture: ComponentFixture<CreateConsultantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateConsultantComponent]
    });
    fixture = TestBed.createComponent(CreateConsultantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
