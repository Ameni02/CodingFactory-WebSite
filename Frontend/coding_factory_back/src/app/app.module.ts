import { NgModule } from '@angular/core';
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
import { ProjectDetailsComponent } from './Modules/PfeSpace/projects/project-details/project-details.component';

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
    ProjectDetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    
    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
