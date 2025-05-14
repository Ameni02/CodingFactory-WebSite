import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SidebarComponent } from './Components/sidebar/sidebar.component';
import { HeaderComponent } from './Components/header/header.component';
import { FooterComponent } from './Components/footer/footer.component';
import { HomeComponent } from './Components/home/home.component';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component';
import { ContainerComponent } from './Components/container/container.component';
import { ProjectsComponent } from './Modules/PfeSpace/projects/projects.component';
import { DeliverablesComponent } from './Modules/PfeSpace/deliverables/deliverables.component';
import { EvaluationsComponent } from './Modules/PfeSpace/evaluations/evaluations.component';
import { CreateProjectComponent } from './Modules/PfeSpace/projects/create-project/create-project.component';
import { ListProjectComponent } from './Modules/PfeSpace/projects/list-project/list-project.component';
import { FormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProjectDetailsComponent } from './Modules/PfeSpace/projects/project-details/project-details.component';
import { ModifyProjectComponent } from './Modules/PfeSpace/projects/modify-project/modify-project.component';
import { DeliverableDetailsComponent } from './Modules/PfeSpace/deliverables/deliverable-details/deliverable-details.component';
import { DeliverableListComponent } from './Modules/PfeSpace/deliverables/deliverable-list/deliverable-list.component';
import { ApplicationsListComponent } from './Modules/PfeSpace/applications/applications-list/applications-list.component';
import { ApplicationDetailsComponent } from './Modules/PfeSpace/applications/application-details/application-details.component';
import { RouterModule } from '@angular/router';
import { CreateConsultantComponent } from './Modules/create-consultant/create-consultant.component';
import { ConsultationlistComponent } from './Components/consultationlist/consultationlist.component';
import { ClientlistComponent } from './Components/clientlist/clientlist.component';
import { ConsultantListComponent } from './Components/consultant-list/consultant-list.component';
import { EditConsultantComponent } from './Components/edit-consultant/edit-consultant.component';
import { ConsultingDashboardComponent } from './Components/consulting-dashboard/consulting-dashboard.component';
import { ConsultationsListComponent } from './Modules/consultations-list/consultations-list.component';
import { AllConsultationsComponent } from './Components/all-consultations/all-consultations.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FullCalendarModule } from '@fullcalendar/angular';


@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    MainPageComponent,
    ContainerComponent,
    ProjectsComponent,
    DeliverablesComponent,
    EvaluationsComponent,
    CreateProjectComponent,
    ListProjectComponent,
    ProjectDetailsComponent,
    ModifyProjectComponent,
    DeliverableDetailsComponent,
    DeliverableListComponent,
    ApplicationsListComponent,
    ApplicationDetailsComponent,
    CreateConsultantComponent,
    ConsultantListComponent,
    ConsultationlistComponent,
    ClientlistComponent,
    EditConsultantComponent,
    ConsultingDashboardComponent,
    ConsultationsListComponent,
    AllConsultationsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    
    // FullCalendar Module
    FullCalendarModule
    
    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
