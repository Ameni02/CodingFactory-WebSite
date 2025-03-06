import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/services/project.service'; 
import { Project } from 'src/app/models/project.model'; 

@Component({
  selector: 'app-offer-list',
  templateUrl: './offer-list.component.html',
  styleUrls: ['./offer-list.component.css'],
})
export class OfferListComponent implements OnInit {
  offers: Project[] = [];
  paginatedOffers: Project[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalPages: number = 1; // Add this property

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => {
        this.offers = data;
        this.totalPages = Math.ceil(this.offers.length / this.itemsPerPage); // Calculate total pages
        this.updatePaginatedOffers();
      },
      error: (error) => console.error('Error loading offers:', error),
    });
  }

  updatePaginatedOffers(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedOffers = this.offers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePaginatedOffers();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePaginatedOffers();
    }
  }
}
