import { Component, Renderer2, Inject,HostListener, OnInit } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import {AuthenticationService} from "../../services/services/authentication.service";
import {Router} from "@angular/router";
import {JwtHelperService} from "@auth0/angular-jwt";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  

  openEventModal() {
    // Try to find the modal element
    const modalElement = this.document.getElementById('modalCreateEvents');

    if (modalElement) {
      // Create a new instance of Bootstrap Modal
      const modal = new (window as any).bootstrap.Modal(modalElement);
      modal.show();
    } else {
      console.error('Modal element not found');
    }
  }
  isAuthenticated = false;
  userId: number | null = null;
  showProfileForm = false;
  lastScrollPosition = 0;
  isHeaderHidden = false;

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private jwtHelper: JwtHelperService,
    private renderer: Renderer2, 
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit(): void {
    this.checkAuthStatus();
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    const currentScrollPosition = window.pageYOffset;

    // Hide header when scrolling down
    if (currentScrollPosition > this.lastScrollPosition && currentScrollPosition > 100) {
      this.isHeaderHidden = true;
    }
    // Show header when scrolling up
    else if (currentScrollPosition < this.lastScrollPosition) {
      this.isHeaderHidden = false;
    }

    this.lastScrollPosition = currentScrollPosition;
  }

  checkAuthStatus(): void {
    const token = localStorage.getItem('authToken');
    this.isAuthenticated = !!token && !this.jwtHelper.isTokenExpired(token);

    if (this.isAuthenticated && token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.userId = decodedToken?.userId || null;
    }
  }
  navigateToProfile(): void {
    if (this.userId) {
      this.router.navigate(['/modify-user']);
    }
  }

  handleSearch(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      const inputElement = event.target as HTMLInputElement;
      const searchTerm = inputElement.value.trim();

      if (searchTerm) {
        console.log('Searching for:', searchTerm);
      }
    }
  }

  toggleProfileForm(): void {
    this.showProfileForm = !this.showProfileForm;
  }
  navigateToEvents() {
    console.log('Navigating to events page');
    this.router.navigate(['/events']);
  }
}

