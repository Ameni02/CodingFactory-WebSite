import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserControllerService } from 'src/app/services/services/user-controller.service';

@Component({
  selector: 'app-modify-user',
  templateUrl: './modify-user.component.html',
  styleUrls: ['./modify-user.component.css']
})
export class ModifyUserComponent implements OnInit {
  userForm: FormGroup;
  selectedFile: File | null = null;
  userId: number | null = null;
  profilePicUrl: string = 'assets/default-avatar.png';
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserControllerService,
    private route: ActivatedRoute,
    public router: Router,
    private cdRef: ChangeDetectorRef
  ) {
    this.userForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.maxLength(50)]],
      lastname: ['', [Validators.required, Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      dateOfBirth: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const token = localStorage.getItem('authToken');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    try {
      const decodedToken = JSON.parse(atob(token.split('.')[1]));
      this.userId = decodedToken.userId;

      if (!this.userId) {
        throw new Error('User ID not found in token');
      }

      this.loadUserData();
    } catch (error) {
      this.handleError('Invalid authentication token', error);
    }
  }

  loadUserData(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.userService.getUserById({ id: this.userId }).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastname,
          email: user.email,
          dateOfBirth: user.dateOfBirth ? new Date(user.dateOfBirth).toISOString().split('T')[0] : ''
        });

        if (user.profilePicUrl) {
          this.profilePicUrl = user.profilePicUrl.startsWith('http')
            ? user.profilePicUrl
            : `http://localhost:8081/api/v1${user.profilePicUrl}`;
        }

        this.isLoading = false;
        this.cdRef.detectChanges();
      },
      error: (error) => {
        this.handleError('Failed to load user data', error);
      }
    });
  }

  uploadProfilePicture(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file first!';
      return;
    }

    if (!this.userId) {
      this.handleError('Invalid user ID');
      return;
    }

    this.isLoading = true;
    this.userService.uploadProfilePicture({
      id: this.userId,
      body: { file: this.selectedFile }
    }).subscribe({
      next: (imagePath) => {
        this.profilePicUrl = imagePath.startsWith('http')
          ? imagePath
          : `http://localhost:8081/api/v1${imagePath}`;
        this.errorMessage = null;
        this.isLoading = false;
      },
      error: (error) => {
        this.handleError('Failed to upload profile picture', error);
      }
    });
  }

  updateUser(): void {
    if (this.userForm.invalid || !this.userId) {
      this.errorMessage = 'Please fill all required fields correctly';
      return;
    }

    this.isLoading = true;
    const userData = this.userForm.value;

    if (userData.dateOfBirth) {
      userData.dateOfBirth = new Date(userData.dateOfBirth);
    }

    this.userService.updateUser({
      id: this.userId,
      body: userData
    }).subscribe({
      next: () => {
        this.errorMessage = null;
        this.isLoading = false;
        alert('Profile updated successfully!');
      },
      error: (error) => {
        this.handleError('Failed to update profile', error);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.profilePicUrl = e.target?.result as string;
        this.cdRef.detectChanges();
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  private handleError(message: string, error?: any): void {
    this.errorMessage = message;
    this.isLoading = false;
    console.error(message, error);

    if (error?.status === 401 || error?.status === 403) {
      localStorage.removeItem('authToken');
      this.router.navigate(['/login']);
    }
  }
}
