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
  totalPages: number = 1;
  searchQuery: string = '';
  
  selectedField: string = '';
  selectedCompany: string = '';
  selectedPositions: string = '';
  
  fields: string[] = ['Technology', 'Business', 'Engineering', 'Design']; // Replace with actual data
  companies: string[] = ['Company A', 'Company B', 'Company C']; // Replace with actual data
  availablePositions: string[] = ['1', '2', '3', '4', '5']; // Available positions

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => {
        this.offers = data;
        this.totalPages = Math.ceil(this.offers.length / this.itemsPerPage);
        this.updatePaginatedOffers();
      },
      error: (error) => console.error('Error loading offers:', error),
    });
  }

  updatePaginatedOffers(): void {
    // Filter offers based on search query
    let filteredOffers = this.offers.filter(offer => 
      offer.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      offer.field.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      offer.companyName.toLowerCase().includes(this.searchQuery.toLowerCase())
    );

    // Apply additional filters based on selection
    if (this.selectedField) {
      filteredOffers = filteredOffers.filter(offer => offer.field === this.selectedField);
    }

    if (this.selectedCompany) {
      filteredOffers = filteredOffers.filter(offer => offer.companyName === this.selectedCompany);
    }

    if (this.selectedPositions) {
      filteredOffers = filteredOffers.filter(offer => offer.numberOfPositions === +this.selectedPositions);
    }

    this.totalPages = Math.ceil(filteredOffers.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedOffers = filteredOffers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  onSearch(): void {
    this.currentPage = 1;
    this.updatePaginatedOffers();
  }

  onFilterChange(): void {
    this.currentPage = 1;
    this.updatePaginatedOffers();
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
