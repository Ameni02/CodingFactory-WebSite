import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultantService } from 'src/app/services/consultant.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-consultant',
  templateUrl: './edit-consultant.component.html',
  styleUrls: ['./edit-consultant.component.css']
})
export class EditConsultantComponent {
  consultant: any = {
    fullName: '',
    email: '',
    specialty: '',
    profileDescription: ''
  };

  originalEmail: string = '';
  emailTaken: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private consultantService: ConsultantService
  ) {}

  ngOnInit(): void {
    const consultantId = Number(this.route.snapshot.paramMap.get('id'));
    if (consultantId) {
      this.consultantService.getConsultantById(consultantId).subscribe({
        next: (data) => {
          this.consultant = data;
          this.originalEmail = data.email;
        },
        error: () => {
          Swal.fire('Error', 'Failed to load consultant', 'error');
          this.router.navigate(['/consultants']);
        }
      });
    }
  }

  validateEmail() {
    if (this.consultant.email && this.consultant.email !== this.originalEmail) {
      this.consultantService.checkEmailExists(this.consultant.email).subscribe({
        next: (exists) => {
          this.emailTaken = exists;
        },
        error: () => {
          this.emailTaken = false;
        }
      });
    } else {
      this.emailTaken = false;
    }
  }
  
  onUpdate() {
    this.consultantService.updateConsultant(this.consultant.id, this.consultant).subscribe({
      next: () => {
        Swal.fire({
          title: 'Success!',
          text: 'Consultant updated successfully!',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          this.router.navigate(['/consultant-list']);
        });
      },
      error: () => {
        Swal.fire('Error', 'Failed to update consultant', 'error');
      }
    });
  }
}
