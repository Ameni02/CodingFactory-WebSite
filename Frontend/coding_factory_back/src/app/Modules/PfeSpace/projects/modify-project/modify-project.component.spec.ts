import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyProjectComponent } from './modify-project.component';

describe('ModifyProjectComponent', () => {
  let component: ModifyProjectComponent;
  let fixture: ComponentFixture<ModifyProjectComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModifyProjectComponent]
    });
    fixture = TestBed.createComponent(ModifyProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
