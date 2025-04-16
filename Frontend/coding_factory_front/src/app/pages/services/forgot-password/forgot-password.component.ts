import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/services/services';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'] // Add corresponding CSS file
})
export class ForgotPasswordComponent {
  email: string = '';
  message: string = '';
  isSuccess: boolean = false;
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(private authService: AuthenticationService) {}

  requestPasswordReset(): void {
    // Validate email format before sending
    if (!this.validateEmail(this.email)) {
      this.errorMessage = 'Please enter a valid email address';
      this.isSuccess = false;
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.message = '';

    this.authService.requestPasswordReset({ email: this.email }).subscribe({
      next: () => {
        // Always show success message even if email doesn't exist (security best practice)
        this.message = 'If an account exists with this email, a password reset link has been sent.';
        this.isSuccess = true;
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        this.isSuccess = false;

        if (error.status === 0) {
          this.errorMessage = 'Network error. Please check your connection.';
        } else if (error.status === 429) {
          this.errorMessage = 'Too many requests. Please try again later.';
        } else {
          // For other errors, show generic message (security best practice)
          this.message = 'If an account exists with this email, a password reset link has been sent.';
          this.isSuccess = true;
        }
      }
    });
  }

  private validateEmail(email: string): boolean {
    const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return re.test(email);
  }
}
