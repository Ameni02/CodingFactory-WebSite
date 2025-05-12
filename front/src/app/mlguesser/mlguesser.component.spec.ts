import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MlguesserComponent } from './mlguesser.component';

describe('MlguesserComponent', () => {
  let component: MlguesserComponent;
  let fixture: ComponentFixture<MlguesserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MlguesserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MlguesserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
