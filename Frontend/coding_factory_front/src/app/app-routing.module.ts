import { NgModule } from '@angular/core';
import { RouterModule , Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';


const routes: Routes = [
  { path: '', component: HomeComponent }, // Default homepage

  // The PfeSpace routes are now handled by the PfeSpace module
  // We only need to keep the old routes for backward compatibility
  { path: 'offers/add', redirectTo: 'pfe-space/offers/add', pathMatch: 'full' },
  { path: 'offers', redirectTo: 'pfe-space/offers', pathMatch: 'full' },
  { path: 'offers/:id', redirectTo: 'pfe-space/offers/:id', pathMatch: 'full' },
  { path: 'applications/new', redirectTo: 'pfe-space/applications/new', pathMatch: 'full' },
  { path: 'applications/:id', redirectTo: 'pfe-space/applications/:id', pathMatch: 'full' },
  { path: 'submissions/new', redirectTo: 'pfe-space/submissions/new', pathMatch: 'full' },
  { path: 'submissions', redirectTo: 'pfe-space/submissions', pathMatch: 'full' },
  { path: 'submissions/:id', redirectTo: 'pfe-space/submissions/:id', pathMatch: 'full' },
  { path: 'evaluations/new', redirectTo: 'pfe-space/evaluations/new', pathMatch: 'full' },
  { path: 'ai-workflow', redirectTo: 'pfe-space/ai-workflow', pathMatch: 'full' },
  { path: 'merge', redirectTo: 'pfe-space/merge', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
