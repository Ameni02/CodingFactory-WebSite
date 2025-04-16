import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {BackComponent} from "./back.component";
import {EventsComponent} from "./pages/events/events.component";
import {FeedbacksComponent} from "./pages/feedbacks/feedbacks.component";
import {ParticipationsComponent} from "./pages/participations/participations.component";

const routes: Routes = [
  {path:'',component:BackComponent,children:[
      {path:'',component:EventsComponent},
      {path:'feedbacks/:id',component:FeedbacksComponent},
      {path:'participations/:id',component:ParticipationsComponent}
    ]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackRoutingModule { }
