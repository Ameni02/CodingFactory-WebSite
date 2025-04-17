import { Component, OnInit, Renderer2 } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'coding_factory_front';
  isAdminRoute = false;
  shouldShowHeader = true;

  constructor(
    private router: Router,
    private renderer: Renderer2
  ) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      console.log('Navigation event:', event.url);
      this.isAdminRoute = event.url.includes('/admin');
      this.shouldShowHeader = !event.url.includes('/admin') &&
                             !event.url.includes('/login') &&
                             !event.url.includes('/register');
      this.updateMainBanner();
    });
  }

  ngOnInit() {
    this.updateMainBanner();
    // Optionally redirect to login if you want to ensure it's always shown first
    if (this.router.url === '/') {
      this.router.navigate(['/login']);
    }

    // Set initial header visibility based on current route
    const currentUrl = this.router.url;
    this.shouldShowHeader = !currentUrl.includes('/admin') &&
                           !currentUrl.includes('/login') &&
                           !currentUrl.includes('/register');
  }

  updateMainBanner() {
    const mainBanner = document.querySelector('.main-banner') as HTMLElement;
    if (mainBanner) {
      if (this.isAdminRoute || this.router.url === '/login') {
        this.renderer.setStyle(mainBanner, 'display', 'none');
      } else {
        this.renderer.setStyle(mainBanner, 'display', 'block');
      }
    }
  }
}
