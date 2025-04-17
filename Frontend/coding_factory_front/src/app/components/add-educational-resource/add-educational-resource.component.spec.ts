import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEducationalResourceComponent } from './add-educational-resource.component';

describe('AddEducationalResourceComponent', () => {
  let component: AddEducationalResourceComponent;
  let fixture: ComponentFixture<AddEducationalResourceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddEducationalResourceComponent]
    });
    fixture = TestBed.createComponent(AddEducationalResourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
