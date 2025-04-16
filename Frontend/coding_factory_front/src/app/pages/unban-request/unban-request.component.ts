import { Component } from '@angular/core';
import { AuthenticationService } from "src/app/services/services";

@Component({
  selector: 'app-unban-request',
  templateUrl: './unban-request.component.html',
  styleUrls: ['./unban-request.component.css']
})
export class UnbanRequestComponent {
  email: string = '';
  message: string = '';
  error: string = '';

  constructor(private authService: AuthenticationService) {}

  requestUnban() {
    this.message = '';
    this.error = '';

    if (!this.email) {
      this.error = 'Veuillez entrer votre email.';
      return;
    }

    this.authService.requestUnban(this.email).subscribe({
      next: (response) => {
        this.message = response;
      },
      error: (err) => {
        this.error = 'Une erreur est survenue. Vérifiez votre email et réessayez.';
      }
    });
  }
}
