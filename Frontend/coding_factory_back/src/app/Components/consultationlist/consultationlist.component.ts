import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Consultation, ConsultationStatus } from 'src/app/models/Consulting';
import { ClientService } from 'src/app/services/client.service';

@Component({
  selector: 'app-consultationlist',
  templateUrl: './consultationlist.component.html',
  styleUrls: ['./consultationlist.component.css']
})
export class ConsultationlistComponent implements OnInit{
  consultations: Consultation[] = [];
  clientId!: number;

  consultationStatus = ConsultationStatus;
  constructor(
    private route: ActivatedRoute,
    private clientService: ClientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.clientId = Number(this.route.snapshot.paramMap.get('clientId'));
    this.loadConsultations();
    console.log(this.clientId);

  }

  loadConsultations() {
    this.clientService.getClientConsultations(this.clientId).subscribe(data => {
      this.consultations = data;
    });
    console.log(this.consultations);
  }

  goBack() {
    this.router.navigate(['/client-management']);
  }

}
