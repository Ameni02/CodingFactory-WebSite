import { NgModule } from '@angular/core';
import { RouterModule , Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { AdminPfeSpaceComponent } from './components/admin/admin-pfespace/admin-pfespace.component';
import { ListTrainingUserComponent } from './components/list-training-user/list-training-user.component';
import { ListTrainingComponent } from './components/list-training/list-training.component';
import { AddTrainingComponent } from './components/add-training/add-training.component';
import { AddEducationalResourceComponent } from './components/add-educational-resource/add-educational-resource.component';
import { ListResourceAdminComponent } from './components/list-resource-admin/list-resource-admin.component';

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
      { path: 'pfespace', component: AdminPfeSpaceComponent }, // PFE Space management
      { path: 'listTrainingAdmin', component: ListTrainingComponent},
      { path: 'addTraining', component: AddTrainingComponent},
      { path: 'addRousource', component: AddEducationalResourceComponent},
      { path: 'listPedagogique', component: ListResourceAdminComponent}
    
    ]
  },

  // The PfeSpace routes are handled by the PfeSpace module
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
  { path: 'merge', redirectTo: 'pfe-space/merge', pathMatch: 'full' },
  { path: 'listTrainingUser', component: ListTrainingUserComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
