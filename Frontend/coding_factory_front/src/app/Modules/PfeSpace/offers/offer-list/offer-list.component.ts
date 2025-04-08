import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/services/project.service';
import { Project } from 'src/app/models/project.model';
import { ToastrService } from 'ngx-toastr';

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
  isLoading: boolean = true;
  searchQuery: string = '';
  selectedField: string = '';
  fields: string[] = [];

  constructor(
    private projectService: ProjectService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.isLoading = true;
    this.projectService.getProjects().subscribe({
      next: (projects: Project[]) => {
        this.offers = projects || [];
        this.fields = [...new Set(this.offers.map(p => p.field))];
        this.updatePaginatedOffers();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading offers:', err);
        this.toastr.error(err.message || 'Failed to load offers');
        this.offers = [];
        this.updatePaginatedOffers();
        this.isLoading = false;
      }
    });
  }

  extractFields(): void {
    this.fields = [...new Set(this.offers.map(offer => offer.field))];
    // Add empty option for "All fields"
    if (this.fields.length > 0 && !this.fields.includes('')) {
      this.fields.unshift('');
    }
  }

  updatePaginatedOffers(): void {
    // Filter offers based on search query
    let filteredOffers = this.offers;
    
    if (this.searchQuery) {
      filteredOffers = filteredOffers.filter(offer => 
        (offer.title?.toLowerCase().includes(this.searchQuery.toLowerCase()) || false) ||
        (offer.field?.toLowerCase().includes(this.searchQuery.toLowerCase()) || false) ||
        (offer.companyName?.toLowerCase().includes(this.searchQuery.toLowerCase()) || false)
      );
    }

    // Apply field filter if selected
    if (this.selectedField) {
      filteredOffers = filteredOffers.filter(offer => offer.field === this.selectedField);
    }

    // Calculate pagination
    this.totalPages = Math.max(1, Math.ceil(filteredOffers.length / this.itemsPerPage));
    this.currentPage = Math.min(this.currentPage, this.totalPages);
    
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

  trackByProjectId(index: number, project: Project): number {
    return project.id;
  }
}