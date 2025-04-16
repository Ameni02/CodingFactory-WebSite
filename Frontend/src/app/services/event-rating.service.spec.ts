import { TestBed } from '@angular/core/testing';

import { EventRatingService } from './event-rating.service';

describe('EventRatingService', () => {
  let service: EventRatingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventRatingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
