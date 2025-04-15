import { NgModule } from '@angular/core';
import { RouterModule , Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  { path: '', component: HomeComponent }, // Default homepage

  // Admin routes
  {
    path: 'admin',
    component: AdminLayoutComponent,
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'users', component: AdminDashboardComponent }, // Placeholder for now
      { path: 'trainings', component: AdminDashboardComponent }, // Placeholder for now
      { path: 'evaluations', component: AdminDashboardComponent }, // Placeholder for now
      { path: 'consulting', component: AdminDashboardComponent }, // Placeholder for now
      { path: 'pfespace', component: AdminDashboardComponent } // Placeholder for now
    ]
  },

  // The PfeSpace routes are now handled by the PfeSpace module
  // We only need to keep the old routes for backward compatibility
  { path: 'offers/add', redirectTo: 'pfe-space/offers/add', pathMatch: 'full' },
  { path: 'offers', redirectTo: 'pfe-space/offers', pathMatch: 'full' },
  { path: 'offers/:id', redirectTo: 'pfe-space/offers/:id', pathMatch: 'full' },
  { path: 'applications/new', redirectTo: 'pfe-space/applications/new', pathMatch: 'full' },
  { path: 'applications/:id', redirectTo: 'pfe-space/applications/:id', pathMatch: 'full' },
  { path: 'submissions/new', redirectTo: 'pfe-space/submissions/new', pathMatch: 'full' },
  { path: 'submissions', redirectTo: 'pfe-space/submissions', pathMatch: 'full' },
  { path: 'submissions/:id', redirectTo: 'pfe-space/submissions/:id', pathMatch: 'full' },
  { path: 'evaluations/new', redirectTo: 'pfe-space/evaluations/new', pathMatch: 'full' },
  { path: 'ai-workflow', redirectTo: 'pfe-space/ai-workflow', pathMatch: 'full' },
  { path: 'merge', redirectTo: 'pfe-space/merge', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
