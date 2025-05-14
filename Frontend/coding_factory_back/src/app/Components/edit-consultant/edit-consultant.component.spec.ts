import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditConsultantComponent } from './edit-consultant.component';

describe('EditConsultantComponent', () => {
  let component: EditConsultantComponent;
  let fixture: ComponentFixture<EditConsultantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditConsultantComponent]
    });
    fixture = TestBed.createComponent(EditConsultantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
