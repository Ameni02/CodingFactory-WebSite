import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchconsultantComponent } from './matchconsultant.component';

describe('MatchconsultantComponent', () => {
  let component: MatchconsultantComponent;
  let fixture: ComponentFixture<MatchconsultantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatchconsultantComponent]
    });
    fixture = TestBed.createComponent(MatchconsultantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
