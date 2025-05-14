import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfMergeComponentComponent } from './pdf-merge-component.component';

describe('PdfMergeComponentComponent', () => {
  let component: PdfMergeComponentComponent;
  let fixture: ComponentFixture<PdfMergeComponentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PdfMergeComponentComponent]
    });
    fixture = TestBed.createComponent(PdfMergeComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
