import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.scss']
})
export class StarRatingComponent {
  @Input() rating: number = 0;
  @Input() maxRating: number = 5;
  @Input() readonly: boolean = false;
  @Output() ratingChange = new EventEmitter<number>();

  stars: number[] = [];

  ngOnInit() {
    this.stars = Array(this.maxRating).fill(0).map((_, i) => i + 1);
  }

  rate(rating: number) {
    if (!this.readonly) {
      this.rating = rating;
      this.ratingChange.emit(rating);
    }
  }

  isActive(star: number): boolean {
    return star <= this.rating;
  }
}
