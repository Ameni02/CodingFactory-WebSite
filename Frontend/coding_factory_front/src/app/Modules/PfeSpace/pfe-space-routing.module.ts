import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Components
import { MainPageComponent } from './main-page/main-page.component';
import { AddOfferComponent } from './offers/add-offer/add-offer.component';
import { OfferListComponent } from './offers/offer-list/offer-list.component';
import { OfferDetailComponent } from './offers/offer-detail/offer-detail.component';
import { ApplicationFormComponent } from './applications/application-form/application-form.component';
import { ApplicationDetailComponent } from './applications/application-detail/application-detail.component';
import { SubmissionFormComponent } from './submissions/submission-form/submission-form.component';
import { SubmissionListComponent } from './submissions/submission-list/submission-list.component';
import { SubmissionDetailComponent } from './submissions/submission-detail/submission-detail.component';
import { PdfMergeComponentComponent } from './pdf-merge-component/pdf-merge-component.component';
import { SupervisorDashboardComponent } from './supervisors/supervisor-dashboard/supervisor-dashboard.component';
import { EvaluationFormComponent } from './evaluations/evaluation-form/evaluation-form.component';
import { AiWorkflowComponent } from './ai-workflow/ai-workflow.component';
// Chatbot is used as a shared component, not in routing

const routes: Routes = [
  {
    path: 'pfe-space',
    children: [
      { path: '', component: MainPageComponent },
      { path: 'offers/add', component: AddOfferComponent },
      { path: 'offers', component: OfferListComponent },
      { path: 'offers/:id', component: OfferDetailComponent },
      { path: 'applications/new', component: ApplicationFormComponent },
      { path: 'applications/:id', component: ApplicationDetailComponent },
      { path: 'submissions/new', component: SubmissionFormComponent },
      { path: 'submissions', component: SubmissionListComponent },
      { path: 'submissions/:id', component: SubmissionDetailComponent },
      { path: 'supervisors/dashboard', component: SupervisorDashboardComponent },
      { path: 'merge', component: PdfMergeComponentComponent },
      { path: 'evaluations/new', component: EvaluationFormComponent },
      { path: 'ai-workflow', component: AiWorkflowComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PfeSpaceRoutingModule { }
