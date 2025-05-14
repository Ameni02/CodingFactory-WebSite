import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css'],
  standalone: true,
  imports: [RouterModule, CommonModule]
})
export class AdminLayoutComponent {
  showSidebar: boolean = false;

  toggleSidebar(): void {
    this.showSidebar = !this.showSidebar;
  }
}
