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

  constructor(
    private router: Router,
    private renderer: Renderer2
  ) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.isAdminRoute = event.url.includes('/admin');
      this.updateMainBanner();
    });
  }

  ngOnInit() {
    this.updateMainBanner();
    // Optionally redirect to login if you want to ensure it's always shown first
    if (this.router.url === '/') {
      this.router.navigate(['/login']);
    }
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
