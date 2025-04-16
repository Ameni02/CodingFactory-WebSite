import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EventsComponent } from "./events/events.component";
import { EventDetailComponent } from "./event-detail/event-detail.component";
import { FrontComponent } from "./front.component";

const routes: Routes = [
  {
    path: '',
    component: FrontComponent,
    children: [
      { path: '', component: EventsComponent },
      { path: 'eventdetail/:id', component: EventDetailComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontRoutingModule { }
