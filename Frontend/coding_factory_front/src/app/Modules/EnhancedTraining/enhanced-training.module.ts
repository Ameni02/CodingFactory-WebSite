import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { FormationListComponent } from './components/formation-list/formation-list.component';
import { FormationDetailComponent } from './components/formation-detail/formation-detail.component';
import { CommentFormComponent } from './components/comment-form/comment-form.component';
import { CommentListComponent } from './components/comment-list/comment-list.component';

import { FormationService } from './services/formation.service';
import { CommentService } from './services/comment.service';

const routes: Routes = [
  { path: 'formations', component: FormationListComponent },
  { path: 'formations/:id', component: FormationDetailComponent },
  { path: '', redirectTo: 'formations', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    FormationListComponent,
    FormationDetailComponent,
    CommentFormComponent,
    CommentListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    FormationService,
    CommentService
  ],
  exports: [
    RouterModule
  ]
})
export class EnhancedTrainingModule { }
