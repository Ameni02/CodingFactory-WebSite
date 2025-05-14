import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateconsultantComponent } from './createconsultant.component';

describe('CreateconsultantComponent', () => {
  let component: CreateconsultantComponent;
  let fixture: ComponentFixture<CreateconsultantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateconsultantComponent]
    });
    fixture = TestBed.createComponent(CreateconsultantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
