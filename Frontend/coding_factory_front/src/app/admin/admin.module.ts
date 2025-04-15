import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

import { AdminLayoutComponent } from '../layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from '../components/admin/admin-dashboard/admin-dashboard.component';
import { AdminPfeSpaceComponent } from '../components/admin/admin-pfespace/admin-pfespace.component';

@NgModule({
  declarations: [
    AdminPfeSpaceComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule
  ],
  exports: [
    AdminPfeSpaceComponent
  ]
})
export class AdminModule { }
