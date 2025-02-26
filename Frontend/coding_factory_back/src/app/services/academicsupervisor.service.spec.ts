import { TestBed } from '@angular/core/testing';

import { AcademicsupervisorService } from './academicsupervisor.service';

describe('AcademicsupervisorService', () => {
  let service: AcademicsupervisorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AcademicsupervisorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
