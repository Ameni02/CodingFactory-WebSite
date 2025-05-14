import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultingDashboardComponent } from './consulting-dashboard.component';

describe('ConsultingDashboardComponent', () => {
  let component: ConsultingDashboardComponent;
  let fixture: ComponentFixture<ConsultingDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConsultingDashboardComponent]
    });
    fixture = TestBed.createComponent(ConsultingDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
