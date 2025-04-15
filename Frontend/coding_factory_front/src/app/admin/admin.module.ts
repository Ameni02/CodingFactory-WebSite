import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { AdminLayoutComponent } from '../layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from '../components/admin/admin-dashboard/admin-dashboard.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: []
})
export class AdminModule { }
