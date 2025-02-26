import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component'; 
import { HomeComponent } from './Components/home/home.component';
import { ListProjectComponent } from './Modules/PfeSpace/projects/list-project/list-project.component';
import { CreateProjectComponent } from './Modules/PfeSpace/projects/create-project/create-project.component';
import { ProjectDetailsComponent } from './Modules/PfeSpace/projects/project-details/project-details.component';
import { ModifyProjectComponent } from './Modules/PfeSpace/projects/modify-project/modify-project.component';
import { ApplicationDetailsComponent } from './Modules/PfeSpace/applications/application-details/application-details.component';
import { ApplicationsListComponent } from './Modules/PfeSpace/applications/applications-list/applications-list.component';
import { DeliverableDetailsComponent } from './Modules/PfeSpace/deliverables/deliverable-details/deliverable-details.component';
import { DeliverableListComponent } from './Modules/PfeSpace/deliverables/deliverable-list/deliverable-list.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'pfe-space', component: MainPageComponent }, // Route for PFE Space
  { path: 'projects', component: ListProjectComponent },
  { path: 'create-project', component: CreateProjectComponent },
  { path: 'project-details/:id', component: ProjectDetailsComponent },
  { path: 'modify-project/:id', component: ModifyProjectComponent },
  { path: 'application-details/:id', component: ApplicationDetailsComponent },
  { path: 'applications-list', component: ApplicationsListComponent }, 
  { path: 'deliverable-details/:id', component: DeliverableDetailsComponent },
  { path: 'deliverable-list', component: DeliverableListComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
