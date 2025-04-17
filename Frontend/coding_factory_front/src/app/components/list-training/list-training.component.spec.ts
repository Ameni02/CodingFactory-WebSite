import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTrainingComponent } from './list-training.component';

describe('ListTrainingComponent', () => {
  let component: ListTrainingComponent;
  let fixture: ComponentFixture<ListTrainingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListTrainingComponent]
    });
    fixture = TestBed.createComponent(ListTrainingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
