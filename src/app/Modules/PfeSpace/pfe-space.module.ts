import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

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
import { EvaluationFormComponent } from './evaluations/evaluation-form/evaluation-form.component';
import { PdfMergeComponentComponent } from './pdf-merge-component/pdf-merge-component.component';
import { ChatbotComponent } from './chatbot/chatbot.component';
import { AiWorkflowComponent } from './ai-workflow/ai-workflow.component';
import { SupervisorDashboardComponent } from './supervisors/supervisor-dashboard/supervisor-dashboard.component';

// Services
import { ProjectService } from './services/project.service';
import { ApplicationService } from './services/application.service';
import { DeliverableService } from './services/deliverable.service';
import { EvaluationService } from './services/evaluation.service';
import { PdfMergeService } from './services/pdf-merge.service';
import { AiWorkflowService } from './services/ai-workflow.service';
import { ChatbotService } from './services/chatbot.service';
import { CvAnalysisService } from './services/cv-analysis.service';
import { AcademicSupervisorService } from './services/academicsupervisor.service';
import { ContentAnalysisService } from './services/content-analysis.service';

// Routes
import { PfeSpaceRoutingModule } from './pfe-space-routing.module';
import { CreateconsultantComponent } from './createconsultant/createconsultant.component';
import { AddClientComponent } from './add-client/add-client.component';
import { ClienListComponent } from './clien-list/clien-list.component';
import { ConsultationrequestComponent } from './consultationrequest/consultationrequest.component';
import { MatchconsultantComponent } from './matchconsultant/matchconsultant.component';

// Configuration is imported in services

@NgModule({
  declarations: [
    MainPageComponent,
    AddOfferComponent,
    OfferListComponent,
    OfferDetailComponent,
    ApplicationFormComponent,
    ApplicationDetailComponent,
    SubmissionFormComponent,
    SubmissionListComponent,
    SubmissionDetailComponent,
    EvaluationFormComponent,
    PdfMergeComponentComponent,
    ChatbotComponent,
    AiWorkflowComponent,
    SupervisorDashboardComponent,
    CreateconsultantComponent,
    AddClientComponent,
    ClienListComponent,
    ConsultationrequestComponent,
    MatchconsultantComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    PfeSpaceRoutingModule
  ],
  providers: [
    ProjectService,
    ApplicationService,
    DeliverableService,
    EvaluationService,
    PdfMergeService,
    AiWorkflowService,
    ChatbotService,
    CvAnalysisService,
    AcademicSupervisorService,
    ContentAnalysisService
  ],
  exports: [
    MainPageComponent,
    AddOfferComponent,
    OfferListComponent,
    OfferDetailComponent,
    ApplicationFormComponent,
    ApplicationDetailComponent,
    SubmissionFormComponent,
    SubmissionListComponent,
    SubmissionDetailComponent,
    EvaluationFormComponent,
    PdfMergeComponentComponent,
    ChatbotComponent,
    AiWorkflowComponent,
    SupervisorDashboardComponent
  ]
})
export class PfeSpaceModule { }
