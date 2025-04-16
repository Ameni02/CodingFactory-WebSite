import { TestBed } from '@angular/core/testing';

import { ParicipationService } from './paricipation.service';

describe('ParicipationService', () => {
  let service: ParicipationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParicipationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
