import { Component, Renderer2, Inject, HostListener, OnInit, AfterViewInit } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AuthenticationService } from "../../services/services/authentication.service";
import { Router, NavigationEnd } from "@angular/router";
import { JwtHelperService } from "@auth0/angular-jwt";
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, AfterViewInit {
  // Authentication properties
  isAuthenticated = false;
  userId: number | null = null;
  showProfileForm = false;

  // Scroll and visibility properties
  lastScrollPosition = 0;
  isHeaderHidden = false;
  isScrolled = false;

  // Mobile menu properties
  isMobileMenuActive = false;

  // Active section tracking
  activeSection: string = 'home';

  // Track if we're on the home page for transparent header
  isHomePage: boolean = false;

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private jwtHelper: JwtHelperService,
    private renderer: Renderer2,
    @Inject(DOCUMENT) private document: Document
  ) {
    // Track route changes to update active section
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.updateActiveSection(event.url);
      this.closeMobileMenu();
    });
  }

  ngOnInit(): void {
    this.checkAuthStatus();
    this.updateActiveSection(this.router.url);
  }

  ngAfterViewInit(): void {
    // Add smooth scrolling for anchor links
    this.setupSmoothScrolling();
  }

  /**
   * Updates the active section based on the current URL
   */
  updateActiveSection(url: string): void {
    // Check if we're on the home page
    this.isHomePage = url === '/' || url === '/home' || url.startsWith('/home');

    if (url.includes('events')) {
      this.activeSection = 'events';
    } else if (url.includes('listTrainingUser')) {
      this.activeSection = 'courses';
    } else if (url.includes('offers')) {
      this.activeSection = 'pfespace';
    } else if (url.includes('fragment=services')) {
      this.activeSection = 'services';
    } else if (url.includes('home')) {
      this.activeSection = 'home';
    } else {
      this.activeSection = 'home';
    }
  }

  /**
   * Sets up smooth scrolling for anchor links
   */
  private setupSmoothScrolling(): void {
    const links = this.document.querySelectorAll('.scroll-to-section a[fragment]');

    links.forEach(link => {
      link.addEventListener('click', (e) => {
        const fragment = (link as HTMLAnchorElement).getAttribute('fragment');
        if (fragment) {
          e.preventDefault();
          const element = this.document.getElementById(fragment);
          if (element) {
            const headerOffset = 80;
            const elementPosition = element.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

            window.scrollTo({
              top: offsetPosition,
              behavior: 'smooth'
            });

            // Update active section
            this.activeSection = fragment;
          }
        }
      });
    });
  }

  /**
   * Handles window scroll events to show/hide header and apply scroll effects
   */
  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    const currentScrollPosition = window.pageYOffset;

    // Update scrolled state for glass morphism effect
    this.isScrolled = currentScrollPosition > 50;

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

  /**
   * Checks if the user is authenticated
   */
  checkAuthStatus(): void {
    const token = localStorage.getItem('authToken');
    this.isAuthenticated = !!token && !this.jwtHelper.isTokenExpired(token);

    if (this.isAuthenticated && token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.userId = decodedToken?.userId || null;
    }
  }

  /**
   * Navigates to the user profile page
   */
  navigateToProfile(): void {
    if (this.userId) {
      this.router.navigate(['/modify-user']);
    }
  }

  /**
   * Handles search input
   */
  handleSearch(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      const inputElement = event.target as HTMLInputElement;
      const searchTerm = inputElement.value.trim();

      if (searchTerm) {
        console.log('Searching for:', searchTerm);
        // Implement search functionality here
      }
    }
  }

  /**
   * Toggles the profile form visibility
   */
  toggleProfileForm(): void {
    this.showProfileForm = !this.showProfileForm;
  }

  /**
   * Toggles the profile form from mobile menu
   */
  toggleProfileFormMobile(): void {
    this.showProfileForm = !this.showProfileForm;
    this.closeMobileMenu();
  }

  /**
   * Navigates to the events page
   */
  navigateToEvents() {
    console.log('Navigating to events page');
    this.router.navigate(['/events']);
    this.activeSection = 'events';
  }

  /**
   * Navigates to events page and closes mobile menu
   */
  navigateToEventsAndCloseMobile() {
    this.navigateToEvents();
    this.closeMobileMenu();
  }

  /**
   * Opens the event modal
   */
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

  /**
   * Toggles the mobile menu
   */
  toggleMobileMenu(): void {
    this.isMobileMenuActive = !this.isMobileMenuActive;

    // Prevent body scrolling when menu is open
    if (this.isMobileMenuActive) {
      this.renderer.addClass(this.document.body, 'menu-open');
    } else {
      this.renderer.removeClass(this.document.body, 'menu-open');
    }
  }

  /**
   * Closes the mobile menu
   */
  closeMobileMenu(): void {
    if (this.isMobileMenuActive) {
      this.isMobileMenuActive = false;
      this.renderer.removeClass(this.document.body, 'menu-open');
    }
  }
}

