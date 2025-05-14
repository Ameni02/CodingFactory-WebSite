import { Component } from '@angular/core';
import { ClientService } from '../services/client.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-client',
  templateUrl: './add-client.component.html',
  styleUrls: ['./add-client.component.css']
})
export class AddClientComponent {
  client = {
    fullName: '',
    email: ''
  };

  constructor(private clientService: ClientService, private router: Router) {}

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.clientService.registerClient(this.client).subscribe({
        next: (res) => {
          console.log('Client registered:', res);
          const clientId = res.id; // Assuming backend returns created client
          this.router.navigate(['/consultation-request', clientId]);
        },
        error: (err) => {
          console.error('Error registering client:', err);
        }
      });
    }
}
}
