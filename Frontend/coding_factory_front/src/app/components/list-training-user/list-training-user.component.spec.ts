import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTrainingUserComponent } from './list-training-user.component';

describe('ListTrainingUserComponent', () => {
  let component: ListTrainingUserComponent;
  let fixture: ComponentFixture<ListTrainingUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListTrainingUserComponent]
    });
    fixture = TestBed.createComponent(ListTrainingUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
