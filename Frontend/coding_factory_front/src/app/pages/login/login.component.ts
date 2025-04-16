import { Component } from "@angular/core";
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services/authentication.service';
import { AuthenticationRequest } from '../../services/models/authentication-request';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: string[] = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {}

  login() {
    this.errorMsg = [];
    console.log("Attempting login with:", this.authRequest);

    this.authService.authenticate({ body: this.authRequest }).subscribe({
      next: (res) => {
        if (!res.token) {
          this.errorMsg = ['Authentication failed: No token received'];
          return;
        }

        console.log("✅ Authentication successful, token received:", res.token);
        localStorage.setItem('authToken', res.token); // ✅ Store token with correct key

        const payload = this.decodeToken(res.token);
        console.log("🔍 Decoded Token Payload:", payload);

        const userRoles: string[] = Array.isArray(payload.authorities) ? payload.authorities : [];
        const userId: number = payload.userId;

        if (!userId) {
          console.error("❌ User ID missing from token payload");
          this.errorMsg = ['Invalid token: No user ID found'];
          return;
        }

        if (userRoles.includes('ADMIN')) {
          console.log("🚀 Redirecting ADMIN to /main");
          this.router.navigate(['/main']);
        } else if (userRoles.includes('USER')) {
          this.router.navigate(['/home']); // ✅ Redirect USERS with valid user ID
        } else {
          console.error("❌ Unknown role, staying on login page");
          this.errorMsg = ['Unauthorized role'];
        }
      },
      error: (err) => {
        console.error("❌ Error during authentication:", err);
        this.errorMsg = err.error?.validationErrors ?? [err.error?.errorMsg ?? 'Unknown error'];
      }
    });
  }

  private decodeToken(token: string): any {
    try {
      return JSON.parse(atob(token.split('.')[1])); // Decode JWT payload
    } catch (error) {
      console.error('⚠️ Invalid token format:', error);
      return {};
    }
  }
  register() {
    console.log("🔄 Attempting to navigate to /register...");
    this.router.navigate(['/register']).then(success => {
      if (success) {
        console.log("✅ Successfully navigated to /register");
      } else {
        console.error("❌ Navigation to /register failed");
      }
    }).catch(err => console.error("🚨 Navigation error:", err));
  }
  forgotPassword() {
    console.log("🔄 Redirecting to forgot password page...");
    this.router.navigate(['/forgot-password']).then(success => {
      if (success) {
        console.log("✅ Successfully navigated to /forgot-password");
      } else {
        console.error("❌ Navigation to /forgot-password failed");
      }
    }).catch(err => console.error("🚨 Navigation error:", err));
  }
  requestUnban() {
    if (!this.authRequest.email) {
      this.errorMsg = ['Please enter your email to request unban.'];
      return;
    }

    this.authService.requestUnban(this.authRequest.email).subscribe({
      next: (response) => {
        console.log('Unban request successful:', response);
        alert('Unban request has been sent successfully!');
      },
      error: (error) => {
        console.error('Unban request error:', error);

        if (error.status === 200) {
          // The backend responded with a success message, but Angular treats it as an error
          alert('Unban request has been sent successfully!');
        } else {
          this.errorMsg = ['Failed to send unban request. Please try again.'];
        }
      }
    });
  }

}
