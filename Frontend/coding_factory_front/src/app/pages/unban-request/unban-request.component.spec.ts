import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnbanRequestComponent } from './unban-request.component';

describe('UnbanRequestComponent', () => {
  let component: UnbanRequestComponent;
  let fixture: ComponentFixture<UnbanRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UnbanRequestComponent]
    });
    fixture = TestBed.createComponent(UnbanRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
