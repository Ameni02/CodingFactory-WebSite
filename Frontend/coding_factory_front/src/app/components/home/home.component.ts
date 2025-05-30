import { Component, OnInit, AfterViewInit, HostListener } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  isScrolled = false;

  constructor() { }

  ngOnInit(): void {
    // Initialize any data or services
  }

  ngAfterViewInit(): void {
    // Add smooth scrolling for anchor links
    this.setupSmoothScrolling();

    // Add animation to elements when they come into view
    this.setupScrollAnimations();
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    // Detect scroll position to trigger animations or style changes
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isScrolled = scrollPosition > 50;
  }

  private setupSmoothScrolling(): void {
    // Get all links with hash
    const links = document.querySelectorAll('a[href^="#"]');

    // Add click event to each link
    links.forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();

        // Get the target element
        const targetId = (link as HTMLAnchorElement).getAttribute('href')?.substring(1);
        if (!targetId) return;

        const targetElement = document.getElementById(targetId);
        if (!targetElement) return;

        // Calculate position to scroll to
        const headerOffset = 80;
        const elementPosition = targetElement.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

        // Smooth scroll to target
        window.scrollTo({
          top: offsetPosition,
          behavior: 'smooth'
        });
      });
    });
  }

  private setupScrollAnimations(): void {
    // Add animation classes to elements when they come into view
    const animateOnScroll = () => {
      const elements = document.querySelectorAll('.animate-on-scroll');

      elements.forEach(element => {
        const elementTop = element.getBoundingClientRect().top;
        const elementVisible = 150;

        if (elementTop < window.innerHeight - elementVisible) {
          element.classList.add('active');
        }
      });
    };

    // Run once on load
    animateOnScroll();

    // Run on scroll
    window.addEventListener('scroll', animateOnScroll);
  }
}
