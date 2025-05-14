import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ConsultantService } from '../services/consultant.service';

@Component({
  selector: 'app-createconsultant',
  templateUrl: './createconsultant.component.html',
  styleUrls: ['./createconsultant.component.css']
})
export class CreateconsultantComponent {
  consultant = {
    fullName: '',
    email: '',
    specialty: ''
  };
  
  constructor(private consultantService: ConsultantService, private router: Router) {}

  onSave() {
    this.consultantService.createConsultant(this.consultant).subscribe({
      next: () => {
        alert('Consultant created successfully!');
       // this.router.navigate(['/consultants']); // redirect if needed
      },
      error: (err) => {
        console.error(err);
        alert('Failed to create consultant.');
      }
    });
  }

}
