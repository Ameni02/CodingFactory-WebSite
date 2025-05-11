import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SegmentationComponent } from './segmentation.component';

describe('SegmentationComponent', () => {
  let component: SegmentationComponent;
  let fixture: ComponentFixture<SegmentationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SegmentationComponent]
    });
    fixture = TestBed.createComponent(SegmentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
