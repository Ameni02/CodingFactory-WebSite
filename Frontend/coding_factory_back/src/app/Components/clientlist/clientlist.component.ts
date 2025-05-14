import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Client } from 'src/app/models/Consulting';
import { ClientService } from 'src/app/services/client.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-clientlist',
  templateUrl: './clientlist.component.html',
  styleUrls: ['./clientlist.component.css']
})
export class ClientlistComponent implements OnInit {
  clients: Client[] = [];

  constructor(private clientService: ClientService, private router: Router) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients() {
    this.clientService.getAllClients().subscribe(data => this.clients = data);
  }

  deleteClient(id: number | undefined) {
    if (id === undefined) return;

    Swal.fire({
      title: 'Are you sure?',
      text: 'This action cannot be undone!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clientService.deleteClient(id).subscribe(() => {
          Swal.fire(
            'Deleted!',
            'The client has been successfully deleted.',
            'success'
          );
          this.loadClients();
        }, () => {
          Swal.fire(
            'Error!',
            'Client with booked consultation cant be deleted.',
            'error'
          );
        });
      }
    });
  }

  viewConsultations(clientId: number | undefined) {
    if (clientId === undefined) return;
    this.router.navigate(['/client-consultations', clientId]);
  }

  editClient(id: number | undefined) {
    if (id === undefined) return;
    this.router.navigate(['/edit-client', id]);
  }
}
