import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { HomeComponent } from './components/home/home.component';

// Import components for direct routing
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ActivateAccountComponent } from './pages/activate-account/activate-account.component';
import { ForgotPasswordComponent } from './pages/services/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/services/reset-password/reset-password.component';
import { UnbanRequestComponent } from './pages/unban-request/unban-request.component';
import { ModifyUserComponent } from './pages/modify-user/modify-user.component';

// Admin components
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { AdminUsersComponent } from './components/admin/admin-user/admin-user.component';
import { AdminPfeSpaceComponent } from './components/admin/admin-pfespace/admin-pfespace.component';

const routes: Routes = [
  // Public routes
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate-account', component: ActivateAccountComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'unban-req', component: UnbanRequestComponent },

  // Authenticated routes
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'modify-user', component: ModifyUserComponent, canActivate: [AuthGuard] },
  { path: 'modify-user/:id', component: ModifyUserComponent, canActivate: [AuthGuard] },

  // Lazy-loaded modules
  { path: 'events', loadChildren: () => import('./Modules/front/front.module').then(m => m.FrontModule) },
  { path: 'back', loadChildren: () => import('./back/back.module').then(m => m.BackModule) },
  {
    path: 'pfe-space',
    loadChildren: () => import('./Modules/PfeSpace/pfe-space.module').then(m => m.PfeSpaceModule),
    canActivate: [AuthGuard]
  },

  // Redirects for backward compatibility
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

  // Admin routes
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'pfespace', component: AdminPfeSpaceComponent },
      // Add other admin routes as needed
      // { path: 'trainings', component: AdminTrainingsComponent },
      // { path: 'evaluations', component: AdminEvaluationsComponent },
      // { path: 'consulting', component: AdminConsultingComponent },
      { path: 'events', loadChildren: () => import('./back/back.module').then(m => m.BackModule) },
      { path: 'events/feedbacks/:id', loadChildren: () => import('./back/back.module').then(m => m.BackModule) },
      { path: 'events/participations/:id', loadChildren: () => import('./back/back.module').then(m => m.BackModule) },
    ]
  },

  // Default redirects
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    enableTracing: false // Set to true for debugging routes
  })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
