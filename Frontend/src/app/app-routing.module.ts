import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SegmentationComponent } from './segmentation/segmentation.component';
import { RecommandationComponent } from './recommandation/recommandation.component';

const routes: Routes = [
  { path: '', loadChildren: () => import('./front/front.module').then(m => m.FrontModule) },
  { path: 'back', loadChildren: () => import('./back/back.module').then(m => m.BackModule) },
  { path: 'segmentation', component: SegmentationComponent },
  { path: 'recommandation', component: RecommandationComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
