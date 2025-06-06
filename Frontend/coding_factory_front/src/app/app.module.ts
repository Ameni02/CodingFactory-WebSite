import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
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

// Admin Components
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';

// Import Modules
// PfeSpaceModule is lazy loaded in app-routing.module.ts
import { AdminModule } from './admin/admin.module';
import { BackModule } from './back/back.module';
// FrontModule is lazy loaded in app-routing.module.ts

// Services
import { DashboardService } from 'src/app/services/dashboard.service';

import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ForgotPasswordComponent } from "./pages/services/forgot-password/forgot-password.component";
import { ResetPasswordComponent } from "./pages/services/reset-password/reset-password.component";
import { UnbanRequestComponent } from "./pages/unban-request/unban-request.component";
import { ModifyUserComponent } from "./pages/modify-user/modify-user.component";
import { ActivateAccountComponent } from "./pages/activate-account/activate-account.component";
import { RegisterComponent } from "./pages/register/register.component";
import { LoginComponent } from "./pages/login/login.component";
import { CodeInputModule } from "angular-code-input";
import { JWT_OPTIONS, JwtHelperService } from "@auth0/angular-jwt";
import { PfeSpaceModule } from "./Modules/PfeSpace/pfe-space.module";
import { LandingPageComponent } from "./pages/landing-page/landing-page.component";

// Training components
import { ListTrainingComponent } from './components/list-training/list-training.component';
import { ListTrainingUserComponent } from './components/list-training-user/list-training-user.component';
import { AddTrainingComponent } from './components/add-training/add-training.component';
import { AddEducationalResourceComponent } from './components/add-educational-resource/add-educational-resource.component';
import { ListResourceAdminComponent } from './components/list-resource-admin/list-resource-admin.component';
import { TrainingDetailComponent } from './components/training-detail/training-detail.component';
import { CommentFormComponent } from './components/comment-form/comment-form.component';
import { CommentListComponent } from './components/comment-list/comment-list.component';


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
    AdminDashboardComponent,
    LoginComponent,
    RegisterComponent,
    ActivateAccountComponent,
    ModifyUserComponent,
    UnbanRequestComponent,
    ResetPasswordComponent,
    ForgotPasswordComponent,
    ListTrainingComponent,
    ListTrainingUserComponent,
    AddTrainingComponent,
    AddEducationalResourceComponent,
    ListResourceAdminComponent,
    TrainingDetailComponent,
    CommentFormComponent,
    CommentListComponent,
    LandingPageComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN',
    }),
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    // PfeSpaceModule is now lazy loaded in app-routing.module.ts
    AdminModule,
    BackModule,
    CodeInputModule,
    AdminLayoutComponent,
    PfeSpaceModule
  ],
  providers: [DashboardService, { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService],
  bootstrap: [AppComponent]
})
export class AppModule { }
