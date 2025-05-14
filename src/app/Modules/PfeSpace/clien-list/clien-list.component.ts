import { Component } from '@angular/core';
import { Consultant } from '../models/Consulting';
import { ConsultantService } from '../services/consultant.service';

@Component({
  selector: 'app-clien-list',
  templateUrl: './clien-list.component.html',
  styleUrls: ['./clien-list.component.css']
})
export class ClienListComponent {
  consultants: Consultant[] = [];
paginatedConsultants: Consultant[] = [];
specialties?: string[] = [];
selectedConsultant: any = null;

searchQuery = '';
selectedSpecialty = '';
currentPage = 1;
pageSize = 6;
totalPages = 1;
isLoading = true;
constructor (private consultantService : ConsultantService){}
ngOnInit() {
  this.consultantService.getAllConsultants().subscribe(data => {
    this.consultants = data;
    this.specialties = [...new Set(this.consultants
      .map(c => c.specialty)
      .filter((s): s is string => !!s))];    this.applyFilters();
    this.isLoading = false;
  });
}

onSearch() {
  this.applyFilters();
}

onFilterChange() {
  this.applyFilters();
}
openModal(consultant: any) {
  this.selectedConsultant = consultant;
}

closeModal() {
  this.selectedConsultant = null;
}
applyFilters() {
  let filtered = this.consultants;

  if (this.searchQuery) {
    const q = this.searchQuery.toLowerCase();
    filtered = filtered.filter(c =>
      c.fullName.toLowerCase().includes(q) || c.specialty.toLowerCase().includes(q)
    );
  }

  if (this.selectedSpecialty) {
    filtered = filtered.filter(c => c.specialty === this.selectedSpecialty);
  }

  this.totalPages = Math.ceil(filtered.length / this.pageSize);
  this.currentPage = 1;
  this.paginate(filtered);
}

paginate(list: Consultant[]) {
  const start = (this.currentPage - 1) * this.pageSize;
  this.paginatedConsultants = list.slice(start, start + this.pageSize);
}

nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
    this.paginate(this.filteredConsultants());
  }
}

prevPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
    this.paginate(this.filteredConsultants());
  }
}

filteredConsultants() {
  let filtered = this.consultants;

  if (this.searchQuery) {
    const q = this.searchQuery.toLowerCase();
    filtered = filtered.filter(c =>
      c.fullName.toLowerCase().includes(q) || c.specialty.toLowerCase().includes(q)
    );
  }

  if (this.selectedSpecialty) {
    filtered = filtered.filter(c => c.specialty === this.selectedSpecialty);
  }

  return filtered;

}
}
