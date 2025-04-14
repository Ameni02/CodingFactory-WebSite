import { TestBed } from '@angular/core/testing';

import { AcademicSupervisorService } from './academicsupervisor.service';

describe('AcademicsupervisorService', () => {
  let service: AcademicSupervisorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AcademicSupervisorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
