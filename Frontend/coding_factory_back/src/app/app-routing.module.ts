import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './Modules/PfeSpace/main-page/main-page.component'; 
import { HomeComponent } from './Components/home/home.component';
import { ListProjectComponent } from './Modules/PfeSpace/projects/list-project/list-project.component';
import { CreateProjectComponent } from './Modules/PfeSpace/projects/create-project/create-project.component';
import { ProjectDetailsComponent } from './Modules/PfeSpace/projects/project-details/project-details.component';


const routes: Routes = [
  { path: '', component: HomeComponent },
  {path: 'pfe-space', component: MainPageComponent }  , // Route for PFE Space
  {path: 'projects', component: ListProjectComponent },
  {path: 'create-project', component: CreateProjectComponent },
  { path: 'project-details/:id', component: ProjectDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
