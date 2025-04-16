import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Public components
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ActivateAccountComponent } from './pages/activate-account/activate-account.component';
import { ResetPasswordComponent } from './pages/services/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './pages/services/forgot-password/forgot-password.component';
import { UnbanRequestComponent } from './pages/unban-request/unban-request.component';

// Authenticated components
import { HomeComponent } from './components/home/home.component';
import { ModifyUserComponent } from './pages/modify-user/modify-user.component';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component';

// Admin components
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { AdminPfeSpaceComponent } from './components/admin/admin-pfespace/admin-pfespace.component';

import { AuthGuard } from './guards/auth.guard';
import {AdminUsersComponent} from "./components/admin/admin-user/admin-user.component";

const routes: Routes = [
  // Public routes (no AuthGuard)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate-account', component: ActivateAccountComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'unban-req', component: UnbanRequestComponent },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    canActivate: [] // Explicitly empty to ensure no guard
  },

  // Authenticated routes (with AuthGuard)
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'modify-user', component: ModifyUserComponent, canActivate: [AuthGuard] },
  { path: 'modify-user/:id', component: ModifyUserComponent, canActivate: [AuthGuard] },
  { path: 'pfe-space', component: MainPageComponent, canActivate: [AuthGuard] },

  // Admin section
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'trainings', component: AdminDashboardComponent },
      { path: 'evaluations', component: AdminDashboardComponent },
      { path: 'consulting', component: AdminDashboardComponent },
      { path: 'pfespace', component: AdminPfeSpaceComponent }
    ]
  },

  // Redirects
   { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    enableTracing: false // Set to true for debugging routes
  })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
