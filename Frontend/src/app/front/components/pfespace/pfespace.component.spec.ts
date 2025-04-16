import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PfespaceComponent } from './pfespace.component';

describe('PfespaceComponent', () => {
  let component: PfespaceComponent;
  let fixture: ComponentFixture<PfespaceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PfespaceComponent]
    });
    fixture = TestBed.createComponent(PfespaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
