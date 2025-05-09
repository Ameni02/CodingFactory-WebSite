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

    // Simulate loading delay
    setTimeout(() => {
      // Mock statistics data
      this.statistics = {
        users: {
          total: 248,
          new: 32,
          active: 186
        },
        trainings: {
          total: 45,
          active: 28,
          draft: 17
        },
        evaluations: {
          total: 156,
          pending: 23,
          completed: 133
        },
        consulting: {
          total: 87,
          ongoing: 14,
          completed: 73
        },
        pfespace: {
          projects: 64,
          applications: 128,
          submissions: 92,
          supervisors: 18
        }
      };

      // Mock recent activities
      this.recentActivities = [
        {
          id: 1,
          type: 'user',
          user: 'Sarah Johnson',
          description: 'created a new account',
          timestamp: new Date(Date.now() - 15 * 60000) // 15 minutes ago
        },
        {
          id: 2,
          type: 'training',
          user: 'Prof. Ahmed Malik',
          description: 'added a new training course "Advanced Machine Learning"',
          timestamp: new Date(Date.now() - 45 * 60000) // 45 minutes ago
        },
        {
          id: 3,
          type: 'pfespace',
          user: 'TechSolutions Inc.',
          description: 'posted a new project "AI-Powered Customer Service Bot"',
          timestamp: new Date(Date.now() - 2 * 3600000) // 2 hours ago
        },
        {
          id: 4,
          type: 'evaluation',
          user: 'Dr. Emma Chen',
          description: 'completed 5 student evaluations',
          timestamp: new Date(Date.now() - 3 * 3600000) // 3 hours ago
        },
        {
          id: 5,
          type: 'consulting',
          user: 'Michael Rodriguez',
          description: 'scheduled a consulting session for tomorrow',
          timestamp: new Date(Date.now() - 5 * 3600000) // 5 hours ago
        },
        {
          id: 6,
          type: 'pfespace',
          user: 'Amal Benali',
          description: 'submitted a project deliverable for review',
          timestamp: new Date(Date.now() - 8 * 3600000) // 8 hours ago
        },
        {
          id: 7,
          type: 'user',
          user: 'Admin',
          description: 'approved 12 new user registrations',
          timestamp: new Date(Date.now() - 24 * 3600000) // 1 day ago
        }
      ];

      this.loading = false;
    }, 800); // Simulate network delay
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

  getActivityColor(type: string): string {
    switch(type) {
      case 'user':
        return '#3498db'; // Blue
      case 'training':
        return '#2ecc71'; // Green
      case 'evaluation':
        return '#e74c3c'; // Red
      case 'consulting':
        return '#f39c12'; // Orange
      case 'pfespace':
        return '#9b59b6'; // Purple
      default:
        return '#95a5a6'; // Gray
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
