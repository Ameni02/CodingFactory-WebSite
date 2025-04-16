import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-hero-section',
  templateUrl: './hero-section.component.html',
  styleUrls: ['./hero-section.component.scss']
})
export class HeroSectionComponent {
  categories = [
    'CONFERENCE',
    'WORKSHOP',
    'WEBINAR',
    'CONCERT',
    'FESTIVAL',
    'SPORT',
    'NETWORKING',
    'CHARITY',
    'EXHIBITION'
  ];
  @Output() searchFilters = new EventEmitter<any>();

  selectedGenre: string = '';
  startDate: string = '';
  endDate: string = '';
  emitFilters() {
    this.searchFilters.emit({
      genre: this.selectedGenre,
      startDate: this.startDate,
      endDate: this.endDate
    });
  }
}
