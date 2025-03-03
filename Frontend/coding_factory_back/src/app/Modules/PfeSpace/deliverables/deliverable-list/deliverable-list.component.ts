import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DeliverableService } from 'src/app/services/deliverable.service';
import { Deliverable } from 'src/app/models/deliverable.model'; 

@Component({
  selector: 'app-deliverable-list',
  templateUrl: './deliverable-list.component.html',
  styleUrls: ['./deliverable-list.component.css'],
})

export class DeliverableListComponent implements OnInit {
  deliverables: Deliverable[] = [];

  constructor(
    private deliverableService: DeliverableService,
    private router: Router // Inject the Router
  ) {}

  ngOnInit(): void {
    this.fetchDeliverables();
  }

  fetchDeliverables(): void {
    this.deliverableService.getDeliverables().subscribe(
      (data) => {
        this.deliverables = data;
      },
      (error) => {
        console.error('Error fetching deliverables:', error);
      }
    );
  }

  goToDetails(id: number): void {
    this.router.navigate(['deliverable-details/:id', id]); // Navigate to the details page
  }

  archiveDeliverable(id: number): void {
    this.deliverableService.archiveDeliverable(id).subscribe(
      () => {
        this.fetchDeliverables(); // Refresh the list after archiving
      },
      (error) => {
        console.error('Error archiving deliverable:', error);
      }
    );
  } }