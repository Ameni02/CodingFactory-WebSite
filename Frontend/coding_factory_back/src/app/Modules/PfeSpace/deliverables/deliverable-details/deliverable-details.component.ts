import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DeliverableService } from 'src/app/services/deliverable.service'; 
import { Deliverable } from 'src/app/models/deliverable.model';

@Component({
  selector: 'app-deliverable-details',
  templateUrl: './deliverable-details.component.html',
  styleUrls: ['./deliverable-details.component.css'],
})
export class DeliverableDetailsComponent implements OnInit {
  deliverable: Deliverable | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private deliverableService: DeliverableService
  ) {}

  ngOnInit(): void {
    this.fetchDeliverableDetails();
  }

  fetchDeliverableDetails(): void {
    const id = this.route.snapshot.paramMap.get('id'); // Get the deliverable ID from the route
    if (id) {
      this.deliverableService.getDeliverableById(+id).subscribe(
        (data) => {
          this.deliverable = data;
        },
        (error) => {
          console.error('Error fetching deliverable details:', error);
        }
      );
    }
  }

  onDownload(filePath: string): void {
    // Implement file download logic here
    console.log('Downloading file:', filePath);
  }

  onUpdate(): void {
    if (this.deliverable) {
      this.router.navigate(['/deliverables', this.deliverable.id, 'edit']);
    }
  }
}