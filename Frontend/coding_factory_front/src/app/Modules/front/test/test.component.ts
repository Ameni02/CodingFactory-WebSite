import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-test',
  template: `
    <div style="padding: 20px; background-color: #f0f0f0; border-radius: 5px; margin: 20px;">
      <h2>Test Component</h2>
      <p>This is a test component to verify routing.</p>
      <p>Route ID: {{ routeId }}</p>
      <p>Current URL: {{ currentUrl }}</p>
    </div>
  `
})
export class TestComponent {
  routeId: string | null = null;
  currentUrl: string = '';

  constructor(private route: ActivatedRoute) {
    this.routeId = this.route.snapshot.paramMap.get('id');
    this.currentUrl = window.location.href;
  }
}
