import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/services/authentication.service';
import {RegistrationRequest} from '../../services/models/registration-request';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {email: '', firstname: '', lastname: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {
  }

  login() {
    this.router.navigate(['login']);
  }

  register() {
    this.errorMsg = [];
    this.authService.register({ body: this.registerRequest })
      .subscribe({
        next: () => {
          console.log('✅ Registration successful! Redirecting to activate account...');
          this.router.navigate(['/activate-account']).then(() => {
            console.log('✅ Successfully navigated to activate-account!');
          }).catch(err => console.error('🚨 Navigation Error:', err));
        },
        error: (err) => {
          console.error('🚨 Registration failed:', err);
          if (err.error.validationErrors) {
            this.errorMsg = err.error.validationErrors;
          } else {
            this.errorMsg = ['An unexpected error occurred. Please try again.'];
          }
        }
      });
  }
  
  
}