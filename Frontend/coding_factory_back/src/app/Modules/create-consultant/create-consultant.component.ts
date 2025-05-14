import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ConsultantService } from 'src/app/services/consultant.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-consultant',
  templateUrl: './create-consultant.component.html',
  styleUrls: ['./create-consultant.component.css']
})
export class CreateConsultantComponent {
  consultant = {
    fullName: '',
    email: '',
    specialty: '',
    profileDescription: ''
  };

  constructor(
    private consultantService: ConsultantService,
    private router: Router
  ) {}

  onSave() {
    const email = this.consultant.email.trim();
    
    this.consultantService.checkEmailExists(email).subscribe({
      next: (exists) => {
        if (exists) {
          Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'Email already exists.',
          });
        } else {
          // Proceed to create consultant
          this.consultantService.createConsultant(this.consultant).subscribe({
            next: () => {
              Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Consultant created successfully!',
              });
              this.router.navigate(['/consultant-list']);
            },
            error: () => {
              Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to create consultant.',
              });
            }
          });
        }
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error validating email.',
        });
      }
    });
  }
}
