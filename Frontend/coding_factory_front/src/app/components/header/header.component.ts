import { Component, Renderer2, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(private renderer: Renderer2, @Inject(DOCUMENT) private document: Document, private router: Router) {}

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

  navigateToEvents() {
    console.log('Navigating to events page');
    this.router.navigate(['/events']);
  }
}