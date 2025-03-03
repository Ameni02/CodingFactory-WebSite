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

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => (this.offers = data),
      error: (error) => console.error('Error loading offers:', error),
    });
  }
}