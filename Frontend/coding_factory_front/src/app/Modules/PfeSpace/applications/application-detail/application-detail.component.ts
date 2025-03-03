import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApplicationService } from 'src/services/application.service';
import { Application } from 'src/app/models/application.model'; 

@Component({
  selector: 'app-application-detail',
  templateUrl: './application-detail.component.html',
  styleUrls: ['./application-detail.component.css'],
})
export class ApplicationDetailComponent implements OnInit {
  application!: Application;

  constructor(
    private route: ActivatedRoute,
    private applicationService: ApplicationService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.applicationService.getApplicationById(id).subscribe({
      next: (data) => (this.application = data),
      error: (error) => console.error('Error loading application details:', error),
    });
  }
}