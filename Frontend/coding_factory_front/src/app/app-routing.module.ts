import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component';
import { HomeComponent } from './components/home/home.component';
import { OfferListComponent  } from 'src/app/Modules/PfeSpace/offers/offer-list/offer-list.component';

import { OfferDetailComponent } from 'src/app/Modules/PfeSpace/offers/offer-detail/offer-detail.component';
import { ApplicationDetailComponent } from 'src/app/Modules/PfeSpace/applications/application-detail/application-detail.component';
import { ApplicationFormComponent } from 'src/app/Modules/PfeSpace/applications/application-form/application-form.component';
import { SubmissionDetailComponent } from 'src/app/Modules/PfeSpace/submissions/submission-detail/submission-detail.component';
import { SubmissionFormComponent } from 'src/app/Modules/PfeSpace/submissions/submission-form/submission-form.component';
import { SubmissionListComponent } from 'src/app/Modules/PfeSpace/submissions/submission-list/submission-list.component';
import { EvaluationFormComponent } from 'src/app/Modules/PfeSpace/evaluations/evaluation-form/evaluation-form.component';
import { AddOfferComponent } from './Modules/PfeSpace/offers/add-offer/add-offer.component';
import { PdfMergeComponentComponent } from './Modules/PfeSpace/pdf-merge-component/pdf-merge-component.component';


const routes: Routes = [
  { path: '', component: HomeComponent }, // Default homepage
  { path: 'pfe-space', component: MainPageComponent },
  { path: 'merge', component: PdfMergeComponentComponent },
  { path: 'offers/add', component: AddOfferComponent },
  { path: 'offers', component: OfferListComponent },
  { path: 'offers/:id', component: OfferDetailComponent },
  { path: 'applications/new', component: ApplicationFormComponent },
  { path: 'applications/:id', component: ApplicationDetailComponent },
  { path: 'submissions/new', component: SubmissionFormComponent },
  { path: 'submissions', component: SubmissionListComponent },
  { path: 'submissions/:id', component: SubmissionDetailComponent },
  { path: 'evaluations/new', component: EvaluationFormComponent },
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
