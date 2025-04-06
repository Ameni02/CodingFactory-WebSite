import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component';
import { HomeComponent } from './components/home/home.component';
import { OfferListComponent } from './Modules/PfeSpace/offers/offer-list/offer-list.component';
import { OfferDetailComponent } from './Modules/PfeSpace/offers/offer-detail/offer-detail.component';
import { ApplicationDetailComponent } from './Modules/PfeSpace/applications/application-detail/application-detail.component';
import { ApplicationFormComponent } from './Modules/PfeSpace/applications/application-form/application-form.component';
import { SubmissionDetailComponent } from './Modules/PfeSpace/submissions/submission-detail/submission-detail.component';
import { SubmissionFormComponent } from './Modules/PfeSpace/submissions/submission-form/submission-form.component';
import { SubmissionListComponent } from './Modules/PfeSpace/submissions/submission-list/submission-list.component';
import { EvaluationFormComponent } from './Modules/PfeSpace/evaluations/evaluation-form/evaluation-form.component';
import { AddOfferComponent } from './Modules/PfeSpace/offers/add-offer/add-offer.component';
import { PdfMergeComponentComponent } from './Modules/PfeSpace/pdf-merge-component/pdf-merge-component.component';
import { SupervisorDashboardComponent } from './Modules/PfeSpace/supervisors/supervisor-dashboard/supervisor-dashboard.component';
import { ChatbotComponent } from './Modules/PfeSpace/chatbot/chatbot.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
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
  { path: 'supervisor/dashboard', component: SupervisorDashboardComponent },
  { path: 'chatbot', component: ChatbotComponent },
  { path: '**', redirectTo: '/home' } // Wildcard route for a 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
