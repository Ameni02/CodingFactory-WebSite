import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackRoutingModule } from './back-routing.module';
import { BackComponent } from './back.component';
import { EventsComponent } from './pages/events/events.component';
import { FeedbacksComponent } from './pages/feedbacks/feedbacks.component';
import { ParticipationsComponent } from './pages/participations/participations.component';
import {ReactiveFormsModule} from "@angular/forms";
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';


@NgModule({
  declarations: [
    BackComponent,
    EventsComponent,
    FeedbacksComponent,
    ParticipationsComponent,
    SidebarComponent,
    HeaderComponent,
    FooterComponent
  ],
  exports: [
    FooterComponent
  ],
  imports: [
    CommonModule,
    BackRoutingModule,
    ReactiveFormsModule
  ]
})
export class BackModule { }
