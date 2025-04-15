import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { TestService } from 'src/services/test.service';
import { AiWorkflowService } from 'src/services/ai-workflow.service';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';


import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import { FooterComponent } from './components/footer/footer.component';
import { CoursesComponent } from './components/courses/courses.component';
import { ServicesComponent } from './components/services/services.component';
import { EventsComponent } from './components/events/events.component';
import { TeamComponent } from './components/team/team.component';
import { PfespaceComponent } from './components/pfespace/pfespace.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import { ContactUsComponent } from './components/contact-us/contact-us.component';
import { MainBannerComponent } from './components/main-banner/main-banner.component';
import { FactsComponent } from './components/facts/facts.component';
import { TestimonialsComponent } from './components/testimonials/testimonials.component';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component';

import { SupervisorDashboardComponent } from './Modules/PfeSpace/supervisors/supervisor-dashboard/supervisor-dashboard.component';
import { AddOfferComponent } from './Modules/PfeSpace/offers/add-offer/add-offer.component';
import { OfferListComponent } from './Modules/PfeSpace/offers/offer-list/offer-list.component';
import { OfferDetailComponent } from './Modules/PfeSpace/offers/offer-detail/offer-detail.component';
import { ApplicationFormComponent } from './Modules/PfeSpace/applications/application-form/application-form.component';
import { ApplicationDetailComponent } from './Modules/PfeSpace/applications/application-detail/application-detail.component';
import { SubmissionFormComponent } from './Modules/PfeSpace/submissions/submission-form/submission-form.component';
import { SubmissionListComponent } from './Modules/PfeSpace/submissions/submission-list/submission-list.component';
import { SubmissionDetailComponent } from './Modules/PfeSpace/submissions/submission-detail/submission-detail.component';
import { EvaluationFormComponent } from './Modules/PfeSpace/evaluations/evaluation-form/evaluation-form.component';
import { PdfMergeComponentComponent } from './Modules/PfeSpace/pdf-merge-component/pdf-merge-component.component';
import { ChatbotComponent } from './Modules/PfeSpace/chatbot/chatbot.component';
import { AiWorkflowComponent } from './Modules/PfeSpace/ai-workflow/ai-workflow.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    FooterComponent,
    CoursesComponent,
    ServicesComponent,
    EventsComponent,
    TeamComponent,
    PfespaceComponent,
    AboutUsComponent,
    ContactUsComponent,
    MainBannerComponent,
    FactsComponent,
    TestimonialsComponent,
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
    SupervisorDashboardComponent,
    PdfMergeComponentComponent,
    ChatbotComponent,
    AiWorkflowComponent,

  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot()
  ],
  providers: [TestService, AiWorkflowService],
  bootstrap: [AppComponent]
})
export class AppModule { }
