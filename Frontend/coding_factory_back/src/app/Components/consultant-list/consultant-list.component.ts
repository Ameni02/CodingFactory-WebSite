import { Component, OnInit } from '@angular/core';
import { ConsultantService } from 'src/app/services/consultant.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-consultant-list',
  templateUrl: './consultant-list.component.html',
  styleUrls: ['./consultant-list.component.css']
})
export class ConsultantListComponent implements OnInit {
  consultants: any[] = [];

  constructor(
    private consultantService: ConsultantService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadConsultants();
  }

  loadConsultants() {
    this.consultantService.getAllConsultants().subscribe(data => this.consultants = data);
  }

  deleteConsultant(id: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.consultantService.deleteConsultant(id).subscribe(() => {
          Swal.fire(
            'Deleted!',
            'The consultant has been deleted.',
            'success'
          );
          this.loadConsultants();
        }, () => {
          Swal.fire(
            'Error!',
            'Consultant already has consultation booked, cant be deleted.',
            'error'
          );
        });
      }
    });
  }

  viewConsultations(consultantId: number) {
    this.router.navigate(['/consultant-consultations', consultantId]);
  }

  editConsultant(id: number) {
    this.router.navigate(['/edit-consultant', id]);
  }
}
