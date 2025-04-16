import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AdminPfeSpaceComponent } from '../components/admin/admin-pfespace/admin-pfespace.component';
import {AdminUsersComponent} from "../components/admin/admin-user/admin-user.component";

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
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
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
