import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

import { AdminLayoutComponent } from '../layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from '../components/admin/admin-dashboard/admin-dashboard.component';
import { AdminPfeSpaceComponent } from '../components/admin/admin-pfespace/admin-pfespace.component';
import {AdminUsersComponent} from "../components/admin/admin-user/admin-user.component";
import {FormsModule} from "@angular/forms";
import {DialogModule} from "primeng/dialog";
import {MultiSelectModule} from "primeng/multiselect";
import {ToastModule} from "primeng/toast";
import {ConfirmDialogModule} from "primeng/confirmdialog";

@NgModule({
  declarations: [
    AdminPfeSpaceComponent,AdminUsersComponent

  ],
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    FormsModule, // This is required for ngModel
    DialogModule,
    MultiSelectModule,
    ToastModule,
    ConfirmDialogModule
  ],
  exports: [
    AdminPfeSpaceComponent,AdminUsersComponent

  ]
})
export class AdminModule { }
