import { Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ParicipationService} from "../../../services/paricipation.service";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-participations',
  templateUrl: './participations.component.html',
  styleUrls: ['./participations.component.scss']
})
export class ParticipationsComponent {
  eventId!: number;
  participations: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private participationService: ParicipationService,
    private toastr: ToastrService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadParticipations();
  }

  loadParticipations() {
    this.participationService.getParticipantsByEvent(this.eventId).subscribe({
      next: (data) => {
        this.participations = data;
      },
      error: (err) => {
        this.toastr.error('Failed to load participants', 'Error');
        console.error('Error loading participants:', err);
      }
    });
  }

  deleteParticipation(participationId: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'Do you really want to remove this participant? This action cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, remove it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.participationService.deleteParticipation(participationId).subscribe({
          next: () => {
            this.toastr.success('Participant removed successfully', 'Success');


            this.participations = this.participations.filter(p => p.id !== participationId);


            Swal.fire('Removed!', 'The participant has been removed.', 'success');
          },
          error: (err: any) => {
            this.toastr.error('Failed to remove participant', 'Error');
            console.error('Error deleting participation:', err);
          }
        });
      }
    });
  }

  goBack() {
    this.router.navigate(['/back']);
  }
}
