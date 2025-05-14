import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardService, DashboardStats, Activity } from '../../../services/dashboard.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  activeSection: string = 'overview';

  statistics: DashboardStats | null = null;

  recentActivities: Activity[] = [];

  loading = false;
  error: string | null = null;

  constructor(
    private router: Router,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = null;
    this.statistics = null;
    this.recentActivities = [];

    this.dashboardService.getDashboardStats().subscribe({
      next: (stats) => {
        this.statistics = stats;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load dashboard statistics. ' + (err.message || 'Please check your connection and try again.');
        this.loading = false;
        console.error('Error loading dashboard stats:', err);
      }
    });

    this.dashboardService.getRecentActivities().subscribe({
      next: (activities) => {
        this.recentActivities = activities;
      },
      error: (err) => {
        console.error('Error loading activities:', err);
        // We don't set the main error here to avoid overriding the stats error
        // Instead, the activities section will just be empty
      }
    });
  }

  getActivityIcon(type: string): string {
    switch(type) {
      case 'user':
        return 'bi-person';
      case 'training':
        return 'bi-book';
      case 'evaluation':
        return 'bi-clipboard-check';
      case 'consulting':
        return 'bi-briefcase';
      case 'pfespace':
        return 'bi-folder';
      default:
        return 'bi-circle';
    }
  }

  navigateToSection(section: string): void {
    switch(section) {
      case 'users':
        this.router.navigate(['/admin/users']);
        break;
      case 'trainings':
        this.router.navigate(['/admin/trainings']);
        break;
      case 'evaluations':
        this.router.navigate(['/admin/evaluations']);
        break;
      case 'consulting':
        this.router.navigate(['/admin/consulting']);
        break;
      case 'pfespace':
        this.router.navigate(['/admin/pfespace']);
        break;
      default:
        this.activeSection = section;
    }
  }

  handleQuickAction(action: string): void {
    switch(action) {
      case 'newUser':
        this.router.navigate(['/admin/users/new']);
        break;
      case 'newTraining':
        this.router.navigate(['/admin/trainings/new']);
        break;
      case 'viewEvaluations':
        this.router.navigate(['/admin/evaluations']);
        break;
      case 'managePFE':
        this.router.navigate(['/admin/pfespace']);
        break;
    }
  }
}
