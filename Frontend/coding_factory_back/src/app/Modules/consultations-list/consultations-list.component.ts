import { Component } from '@angular/core';
import { ClientService } from 'src/app/services/client.service';

@Component({
  selector: 'app-consultations-list',
  templateUrl: './consultations-list.component.html',
  styleUrls: ['./consultations-list.component.css']
})
export class ConsultationsListComponent {
  consultations: any[] = [];

  constructor(private consultationService: ClientService) {}

  ngOnInit(): void {
    this.loadConsultations();
  }

  loadConsultations() {
    this.consultationService.getAllConsultations().subscribe({
      next: (data) => {
        this.consultations = data;
      },
      error: (err) => {
        console.error('Failed to load consultations', err);
      }
    });
  }

}
